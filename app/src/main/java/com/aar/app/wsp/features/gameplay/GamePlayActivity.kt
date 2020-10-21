package com.aar.app.wsp.features.gameplay

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aar.app.wsp.R
import com.aar.app.wsp.WordSearchApp
import com.aar.app.wsp.commons.DurationFormatter.fromInteger
import com.aar.app.wsp.commons.Util
import com.aar.app.wsp.commons.gone
import com.aar.app.wsp.commons.orZero
import com.aar.app.wsp.commons.visible
import com.aar.app.wsp.custom.LetterBoard.OnLetterSelectionListener
import com.aar.app.wsp.custom.StreakView.StreakLine
import com.aar.app.wsp.features.FullscreenActivity
import com.aar.app.wsp.features.SoundPlayer
import com.aar.app.wsp.features.gameover.GameOverActivity
import com.aar.app.wsp.features.gameplay.GamePlayViewModel.*
import com.aar.app.wsp.model.Difficulty
import com.aar.app.wsp.model.GameData
import com.aar.app.wsp.model.GameMode
import com.aar.app.wsp.model.UsedWord
import kotlinx.android.synthetic.main.activity_game_play.*
import kotlinx.android.synthetic.main.partial_game_complete.*
import kotlinx.android.synthetic.main.partial_game_content.*
import kotlinx.android.synthetic.main.partial_game_header.*
import javax.inject.Inject

class GamePlayActivity : FullscreenActivity() {
    @Inject
    lateinit var soundPlayer: SoundPlayer

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: GamePlayViewModel by viewModels { viewModelFactory }

    private var letterAdapter: ArrayLetterGridDataAdapter? = null
    private var popupTextAnimation: Animation? = null

    private val extraGameMode: GameMode by lazy {
        (intent.extras?.get(EXTRA_GAME_MODE) as? GameMode) ?: GameMode.Normal
    }

    private val extraDifficulty: Difficulty by lazy {
        (intent.extras?.get(EXTRA_GAME_DIFFICULTY) as? Difficulty) ?: Difficulty.Easy
    }

    private val extraGameThemeId: Int by lazy {
        intent.extras?.getInt(EXTRA_GAME_THEME_ID).orZero()
    }

    private val extraRowCount: Int by lazy {
        intent.extras?.getInt(EXTRA_ROW_COUNT).orZero()
    }

    private val extraColumnCount: Int by lazy {
        intent.extras?.getInt(EXTRA_COL_COUNT).orZero()
    }

    private val extraGameId: Int by lazy {
        intent.extras?.getInt(EXTRA_GAME_DATA_ID).orZero()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_play)
        (application as WordSearchApp).appComponent.inject(this)

        initViews()
        initViewModel()

        loadOrGenerateNewGame()
    }

    private fun initViews() {
        text_current_selected_word.setInAnimation(this, android.R.anim.slide_in_left)
        text_current_selected_word.setOutAnimation(this, android.R.anim.slide_out_right)
        letter_board.streakView.setEnableOverrideStreakLineColor(preferences.grayscale())
        letter_board.streakView.setOverrideStreakLineColor(resources.getColor(R.color.gray))
        letter_board.selectionListener = object : OnLetterSelectionListener {
            override fun onSelectionBegin(streakLine: StreakLine, str: String) {
                streakLine.color = Util.getRandomColorWithAlpha(170)
                text_selection_layout.visible()
                text_selection.text = str
            }

            override fun onSelectionDrag(streakLine: StreakLine, str: String) {
                text_selection.text = if (str.isEmpty()) "..." else str
            }

            override fun onSelectionEnd(streakLine: StreakLine, str: String) {
                viewModel.answerWord(str, STREAK_LINE_MAPPER.revMap(streakLine), preferences.reverseMatching())
                text_selection_layout.gone()
                text_selection.text = str
            }
        }

        if (!preferences.showGridLine()) {
            letter_board.gridLineBackground.visibility = View.INVISIBLE
        } else {
            letter_board.gridLineBackground.visible()
        }
        letter_board.streakView.isSnapToGrid = preferences.snapToGrid
        text_game_finished.gone()
        popupTextAnimation = AnimationUtils.loadAnimation(this, R.anim.popup_text)
        popupTextAnimation?.duration = 1000
        popupTextAnimation?.interpolator = DecelerateInterpolator()
        popupTextAnimation?.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                text_popup_correct_word.gone()
                text_popup_correct_word.text = ""
            }
        })
    }

    private fun initViewModel() {
        viewModel.onTimer.observe(this, Observer { duration: Int -> showDuration(duration) })
        viewModel.onCountDown.observe(this, Observer { countDown: Int -> showCountDown(countDown) })
        viewModel.onGameState.observe(this, Observer { gameState: GameState -> onGameStateChanged(gameState) })
        viewModel.onAnswerResult.observe(this, Observer { answerResult: AnswerResult -> onAnswerResult(answerResult) })
        viewModel.onCurrentWordChanged.observe(this, Observer { usedWord: UsedWord ->
            text_current_selected_word.setText(usedWord.string)
            progress_word_duration.max = usedWord.maxDuration * 100
            animateProgress(progress_word_duration, usedWord.remainingDuration * 100)
        })
        viewModel.onCurrentWordCountDown.observe(this, Observer { duration: Int -> animateProgress(progress_word_duration, duration * 100) })
    }

    private fun loadOrGenerateNewGame() {
        if (shouldOpenExistingGameData()) {
            viewModel.loadGameRound(extraGameId)
        } else {
            viewModel.generateNewGameRound(
                rowCount = extraRowCount,
                colCount = extraColumnCount,
                gameThemeId = extraGameThemeId,
                gameMode = extraGameMode,
                difficulty = extraDifficulty
            )
        }
    }

    private fun shouldOpenExistingGameData(): Boolean {
        return intent.extras?.containsKey(EXTRA_GAME_DATA_ID) ?: false
    }

    override fun onStart() {
        super.onStart()
        viewModel.resumeGame()
    }

    override fun onStop() {
        super.onStop()
        viewModel.pauseGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopGame()
    }

    private fun animateProgress(progressBar: ProgressBar?, progress: Int) {
        val anim = ObjectAnimator.ofInt(progressBar, "progress", progress)
        anim.duration = 250
        anim.start()
    }

    private fun onAnswerResult(answerResult: AnswerResult) {
        if (answerResult.correct) {
            val item = findUsedWordViewItemByUsedWordId(answerResult.usedWord?.id.orZero())
            if (item != null) {
                val uw = answerResult.usedWord
                if (preferences.grayscale()) {
                    uw?.answerLine?.color = resources.getColor(R.color.gray)
                }

                val str = item.findViewById<TextView>(R.id.textStr)
                item.background.setColorFilter(uw?.answerLine!!.color, PorterDuff.Mode.MULTIPLY)
                str.text = uw.string
                str.setTextColor(Color.WHITE)
                str.paintFlags = str.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                item.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_out))
                text_popup_correct_word.visible()
                text_popup_correct_word.text = uw.string.orEmpty()
                text_popup_correct_word.startAnimation(popupTextAnimation)
            }
            showAnsweredWordsCount(answerResult.totalAnsweredWord)
            soundPlayer.play(SoundPlayer.Sound.Correct)
        } else {
            letter_board.popStreakLine()
            soundPlayer.play(SoundPlayer.Sound.Wrong)
        }
    }

    private fun onGameStateChanged(gameState: GameState) {
        showLoading(false, null)
        when (gameState) {
            is Generating -> {
                var text = getString(R.string.text_generating)
                text = text.replace(":row".toRegex(), gameState.rowCount.toString())
                text = text.replace(":col".toRegex(), gameState.colCount.toString())
                showLoading(true, text)
            }
            is Loading -> {
                showLoading(true, getString(R.string.lbl_load_game_data))
            }
            is Finished -> {
                showFinishGame(gameState)
            }
            is Playing -> {
                gameState.gameData?.let { onGameRoundLoaded(it) }
            }
        }
    }

    private fun onGameRoundLoaded(gameData: GameData) {
        if (gameData.isFinished) {
            letter_board.streakView.isInteractive = false
            text_game_finished.visible()
            layout_complete_popup.visible()
            text_complete_popup.setText(R.string.lbl_complete)
        } else if (gameData.isGameOver) {
            letter_board.streakView.isInteractive = false
            layout_complete_popup.visible()
            text_complete_popup.setText(R.string.lbl_game_over)
        }
        showLetterGrid(gameData.grid!!.array)
        showDuration(gameData.duration)
        showUsedWords(gameData.usedWords, gameData)
        showWordsCount(gameData.usedWords.size)
        showAnsweredWordsCount(gameData.answeredWordsCount)
        doneLoadingContent()
        when {
            gameData.gameMode === GameMode.CountDown -> {
                progress_overall_duration.visible()
                progress_overall_duration.max = gameData.maxDuration * PROGRESS_SCALE
                progress_overall_duration.progress = gameData.remainingDuration * PROGRESS_SCALE
                layout_current_selected_word.gone()
            }
            gameData.gameMode === GameMode.Marathon -> {
                progress_overall_duration.gone()
                layout_current_selected_word.visible()
            }
            else -> {
                progress_overall_duration.gone()
                layout_current_selected_word.gone()
            }
        }
    }

    private fun tryScale() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val boardWidth = letter_board.width
        val screenWidth = metrics.widthPixels
        if (preferences.autoScaleGrid() || boardWidth > screenWidth) {
            val scale = screenWidth.toFloat() / boardWidth.toFloat()
            letter_board.scale(scale, scale)
        }
    }

    private fun doneLoadingContent() {
        // call tryScale() on the next render frame
        Handler().postDelayed({ tryScale() }, 100)
    }

    private fun showLoading(enable: Boolean, text: String?) {
        if (enable) {
            loading.visible()
            loadingText.visible()
            content_layout.gone()
            loadingText.text = text
        } else {
            loading.gone()
            loadingText.gone()
            if (content_layout.visibility == View.GONE) {
                content_layout.visible()
                content_layout.scaleY = .5f
                content_layout.alpha = 0f
                content_layout.animate()
                    .scaleY(1f)
                    .setDuration(300)
                    .start()
                content_layout.animate()
                    .alpha(1f)
                    .setDuration(600)
                    .start()
            }
        }
    }

    private fun showLetterGrid(grid: Array<CharArray>) {
        if (letterAdapter == null) {
            letterAdapter = ArrayLetterGridDataAdapter(grid)
            letterAdapter?.let {
                letter_board.dataAdapter = it
            }
        } else {
            letterAdapter?.grid = grid
        }
    }

    private fun showDuration(duration: Int) {
        text_overall_duration.text = fromInteger(duration)
    }

    private fun showCountDown(countDown: Int) {
        animateProgress(progress_overall_duration, countDown * PROGRESS_SCALE)
    }

    private fun showUsedWords(usedWords: List<UsedWord>, gameData: GameData) {
        flexbox_layout.removeAllViews()
        for (uw in usedWords) {
            flexbox_layout.addView(createUsedWordTextView(uw, gameData))
        }
    }

    private fun showAnsweredWordsCount(count: Int) {
        text_answered_string_count.text = count.toString()
    }

    private fun showWordsCount(count: Int) {
        text_words_count.text = count.toString()
    }

    private fun showFinishGame(state: Finished) {
        letter_board.streakView.isInteractive = false
        val anim = AnimationUtils.loadAnimation(this, R.anim.game_complete)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = 500
        anim.startOffset = 1000
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                Handler().postDelayed({
                    val intent = Intent(this@GamePlayActivity, GameOverActivity::class.java)
                    intent.putExtra(GameOverActivity.EXTRA_GAME_ROUND_ID, state.gameData?.id.orZero())
                    startActivity(intent)
                    finish()
                }, 800)
            }
        })
        if (state.win) {
            text_complete_popup.setText(R.string.lbl_complete)
            Handler().postDelayed({ soundPlayer.play(SoundPlayer.Sound.Winning) }, 600)
        } else {
            text_complete_popup.setText(R.string.lbl_game_over)
            Handler().postDelayed({ soundPlayer.play(SoundPlayer.Sound.Lose) }, 600)
        }
        layout_complete_popup.visible()
        layout_complete_popup.startAnimation(anim)
    }

    //
    private fun createUsedWordTextView(usedWord: UsedWord, gameData: GameData): View {
        val view = layoutInflater.inflate(R.layout.item_word, flexbox_layout, false)
        val str = view.findViewById<TextView>(R.id.textStr)
        if (usedWord.isAnswered) {
            if (preferences.grayscale()) {
                usedWord.answerLine?.color = resources.getColor(R.color.gray)
            }

            view.background.setColorFilter(usedWord.answerLine!!.color, PorterDuff.Mode.MULTIPLY)
            str.text = usedWord.string
            str.setTextColor(Color.WHITE)
            str.paintFlags = str.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            letter_board.addStreakLine(STREAK_LINE_MAPPER.map(usedWord.answerLine!!))
        } else {
            if (gameData.gameMode === GameMode.Hidden) {
                str.text = getHiddenMask(usedWord.string)
            } else {
                str.text = usedWord.string
            }
        }
        view.tag = usedWord.id
        return view
    }

    private fun getHiddenMask(string: String): String {
        val sb = StringBuilder(string.length)
        for (i in string.indices) sb.append(resources.getString(R.string.hidden_mask))
        return sb.toString()
    }

    private fun findUsedWordViewItemByUsedWordId(usedWordId: Int): View? {
        for (i in 0 until flexbox_layout.childCount) {
            val v = flexbox_layout.getChildAt(i)
            val id = v.tag as Int
            if (id == usedWordId) {
                return v
            }
        }
        return null
    }

    companion object {
        const val EXTRA_GAME_DIFFICULTY = "game_max_duration"
        const val EXTRA_GAME_DATA_ID = "game_data_id"
        const val EXTRA_GAME_MODE = "game_mode"
        const val EXTRA_GAME_THEME_ID = "game_theme_id"
        const val EXTRA_ROW_COUNT = "row_count"
        const val EXTRA_COL_COUNT = "col_count"
        private val STREAK_LINE_MAPPER = StreakLineMapper()
        private const val PROGRESS_SCALE = 100
    }
}
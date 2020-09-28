package com.aar.app.wsp.features.gameover

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.aar.app.wsp.R
import com.aar.app.wsp.WordSearchApp
import com.aar.app.wsp.commons.DurationFormatter.fromInteger
import com.aar.app.wsp.features.FullscreenActivity
import com.aar.app.wsp.features.gameplay.GamePlayActivity
import com.aar.app.wsp.model.GameData
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameOverActivity : FullscreenActivity() {
    @JvmField
    @Inject
    var mViewModelFactory: ViewModelProvider.Factory? = null

    @JvmField
    @BindView(R.id.textCongrat)
    var mTextCongrat: TextView? = null

    @JvmField
    @BindView(R.id.game_stat_text)
    var mTextGameStat: TextView? = null
    private lateinit var viewModel: GameOverViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        ButterKnife.bind(this)
        (application as WordSearchApp).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(GameOverViewModel::class.java)
        viewModel.onGameDataLoaded.observe(this, Observer { gameData ->
            gameData?.let {
                showGameStat(it)
            }
        })
        viewModel.onGameDataReset.observe(this, Observer { gameDataId ->
            val intent = Intent(this@GameOverActivity, GamePlayActivity::class.java)
            intent.putExtra(GamePlayActivity.EXTRA_GAME_DATA_ID, gameDataId)
            startActivity(intent)
            finish()
        })

        intent.extras?.getInt(EXTRA_GAME_ROUND_ID)?.let { gameId ->
            lifecycleScope.launch { viewModel.loadData(gameId) }
        }
    }

    @OnClick(R.id.main_menu_btn)
    fun onMainMenuClick() {
        onBackPressed()
    }

    @OnClick(R.id.btnReplay)
    fun onReplayClick() {
        lifecycleScope.launch {
            viewModel.resetCurrentGameData()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToMainMenu()
    }

    private fun goToMainMenu() {
        if (preferences.deleteAfterFinish()) {
            lifecycleScope.launch { viewModel.deleteGameRound() }
        }
        NavUtils.navigateUpTo(this, Intent())
        finish()
    }

    private fun showGameStat(gd: GameData) {
        if (gd.isGameOver) {
            mTextCongrat!!.setText(R.string.lbl_game_over)
            mTextGameStat!!.visibility = View.GONE
        } else {
            val strGridSize = gd.grid!!.rowCount.toString() + " x " + gd.grid!!.colCount
            var str = getString(R.string.finish_text)
            str = str.replace(":gridSize".toRegex(), strGridSize)
            str = str.replace(":uwCount".toRegex(), gd.usedWords.size.toString())
            str = str.replace(":duration".toRegex(), fromInteger(gd.duration))
            mTextCongrat!!.setText(R.string.congratulations)
            mTextGameStat!!.visibility = View.VISIBLE
            mTextGameStat!!.text = str
        }
    }

    companion object {
        const val EXTRA_GAME_ROUND_ID = "com.paperplanes.wsp.presentation.ui.activity.GameOverActivity"
    }
}
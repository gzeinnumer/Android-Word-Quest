package com.aar.app.wsp.features.gamehistory

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import com.aar.app.wsp.R
import com.aar.app.wsp.WordSearchApp
import com.aar.app.wsp.custom.easyadapter.MultiTypeAdapter
import com.aar.app.wsp.features.FullscreenActivity
import com.aar.app.wsp.features.ViewModelFactory
import com.aar.app.wsp.features.gameplay.GamePlayActivity
import com.aar.app.wsp.model.GameDataInfo
import kotlinx.android.synthetic.main.activity_game_history.*
import javax.inject.Inject

class GameHistoryActivity : FullscreenActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: GameHistoryViewModel by viewModels { viewModelFactory }
    private val adapter: MultiTypeAdapter = MultiTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_history)
        (application as WordSearchApp).appComponent.inject(this)
        ButterKnife.bind(this)

        initRecyclerView()
        viewModel.onGameDataInfoLoaded.observe(this, Observer { gameDataInfoList: List<GameDataInfo> ->
            onGameDataInfoLoaded(gameDataInfoList)
        })
        btnClear.setOnClickListener {
            viewModel.clear()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadGameHistory()
    }

    private fun onGameDataInfoLoaded(gameDataInfos: List<GameDataInfo>) {
        if (gameDataInfos.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
        } else {
            textEmpty.visibility = View.GONE
        }
        adapter.setItems(gameDataInfos)
    }

    private fun initRecyclerView() {
        val gameDataAdapterDelegate = GameDataAdapterDelegate()
        gameDataAdapterDelegate.onClickListener = object : GameDataAdapterDelegate.OnClickListener {
            override fun onClick(gameDataInfo: GameDataInfo?) {
                val intent = Intent(this@GameHistoryActivity, GamePlayActivity::class.java)
                intent.putExtra(GamePlayActivity.EXTRA_GAME_DATA_ID, gameDataInfo!!.id)
                startActivity(intent)
            }

            override fun onDeleteClick(gameDataInfo: GameDataInfo?) {
                gameDataInfo?.let { viewModel.deleteGameData(it) }
            }
        }

        adapter.addDelegate(gameDataAdapterDelegate)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}
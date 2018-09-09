package com.aar.app.wordsearch.features.gamethemeselector;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aar.app.wordsearch.R;
import com.aar.app.wordsearch.WordSearchApp;
import com.aar.app.wordsearch.easyadapter.MultiTypeAdapter;
import com.aar.app.wordsearch.easyadapter.SimpleAdapterDelegate;
import com.aar.app.wordsearch.features.ViewModelFactory;
import com.aar.app.wordsearch.model.GameTheme;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ThemeSelectorActivity extends AppCompatActivity {

    public static final String EXTRA_THEME_ID = "game_theme_id";

    @BindView(R.id.rvThemes)
    RecyclerView mRvThemes;
    private MultiTypeAdapter mAdapter;

    @Inject
    ViewModelFactory mViewModelFactory;
    ThemeSelectorViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selector);

        ((WordSearchApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);

        mAdapter = new MultiTypeAdapter();
        mAdapter.addDelegate(
                GameTheme.class,
                R.layout.item_theme_list,
                (model, holder) -> holder.<TextView>find(R.id.textTheme).setText(model.getName()),
                (model, view) -> onItemClick(model)
        );

        mRvThemes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvThemes.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRvThemes.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ThemeSelectorViewModel.class);
        mViewModel.getOnGameThemeLoaded().observe(this, mAdapter::setItems);
        mViewModel.loadThemes();
    }

    @OnClick(R.id.btnAllTheme)
    public void onAllThemeClick() {
        onItemClick(GameTheme.NONE);
    }

    private void onItemClick(GameTheme theme) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_THEME_ID, theme.getId());
        setResult(RESULT_OK, intent);
        finish();
    }


}

package com.aar.app.wordsearch.features.gamethemeselector;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aar.app.wordsearch.R;
import com.aar.app.wordsearch.WordSearchApp;
import com.aar.app.wordsearch.easyadapter.MultiTypeAdapter;
import com.aar.app.wordsearch.features.FullscreenActivity;
import com.aar.app.wordsearch.features.ViewModelFactory;
import com.aar.app.wordsearch.model.GameTheme;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;

public class ThemeSelectorActivity extends FullscreenActivity {

    public static final String EXTRA_THEME_ID = "game_theme_id";

    @BindView(R.id.loadingLayout) ViewGroup mLoadingLayout;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.rvThemes) RecyclerView mRvThemes;
    @BindView(R.id.textRev) TextView mTextRev;

    @Inject
    ViewModelFactory mViewModelFactory;
    ThemeSelectorViewModel mViewModel;

    private Disposable mUpdateDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selector);

        ((WordSearchApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);

        MultiTypeAdapter adapter = new MultiTypeAdapter();
        adapter.addDelegate(
                GameThemeItem.class,
                R.layout.item_theme_list,
                (model, holder) -> {
                    holder.<TextView>find(R.id.textTheme).setText(model.getName());
                    holder.<TextView>find(R.id.textCount).setText(model.getWordsCount() + " words");
                },
                (model, view) -> onItemClick(model.getId())
        );

        mRvThemes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvThemes.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRvThemes.setAdapter(adapter);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ThemeSelectorViewModel.class);
        mViewModel.getOnGameThemeLoaded().observe(this, gameThemes -> {
            adapter.setItems(gameThemes);
            mRvThemes.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        });
        updateRevisionNumber();

        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUpdateDisposable != null) {
            mUpdateDisposable.dispose();
        }
    }

    @OnClick(R.id.btnAllTheme)
    public void onAllThemeClick() {
        onItemClick(GameTheme.NONE.getId());
    }

    @OnClick(R.id.btnUpdate)
    public void onUpdateClick() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mRvThemes.setEnabled(false);
        mUpdateDisposable = mViewModel.updateData()
                .subscribe(responseType -> {
                            updateRevisionNumber();
                            mLoadingLayout.setVisibility(View.GONE);
                            String message;
                            if (responseType == ThemeSelectorViewModel.ResponseType.NoUpdate) {
                                message = "You're already up to date";
                            } else {
                                message = "Successfully updated";
                            }
                            Toast.makeText(
                                    ThemeSelectorActivity.this,
                                    message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }, throwable -> {
                            Toast.makeText(
                                    ThemeSelectorActivity.this,
                                    "Error: Please check your internet connection",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                );
    }

    private void loadData() {
        mRvThemes.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mViewModel.loadThemes();
    }

    private void onItemClick(int themeId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_THEME_ID, themeId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateRevisionNumber() {
        mTextRev.setText(String.valueOf(mViewModel.getLastDataRevision()));
    }
}

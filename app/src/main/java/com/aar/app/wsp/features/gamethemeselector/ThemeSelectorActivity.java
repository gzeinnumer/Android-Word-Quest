package com.aar.app.wsp.features.gamethemeselector;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aar.app.wsp.R;
import com.aar.app.wsp.WordSearchApp;
import com.aar.app.wsp.custom.easyadapter.MultiTypeAdapter;
import com.aar.app.wsp.features.FullscreenActivity;
import com.aar.app.wsp.features.ViewModelFactory;
import com.aar.app.wsp.model.GameTheme;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class ThemeSelectorActivity extends FullscreenActivity {

    public static final String EXTRA_THEME_ID = "game_theme_id";
    public static final String EXTRA_ROW_COUNT = "row_count";
    public static final String EXTRA_COL_COUNT = "col_count";

    @BindView(R.id.loadingLayout) ViewGroup mLoadingLayout;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.rvThemes) RecyclerView mRvThemes;
    @BindView(R.id.textRev) TextView mTextRev;
    @BindView(R.id.btnUpdate) Button mBtnUpdate;

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
                    holder.<TextView>find(R.id.textCount).setText(
                            getString(R.string.text_words)
                                    .replaceAll(":count", String.valueOf(model.getWordsCount()))
                    );
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
        mBtnUpdate.setEnabled(false);
        mUpdateDisposable = mViewModel.updateData()
                .subscribe(responseType -> {
                            updateRevisionNumber();
                            mLoadingLayout.setVisibility(View.GONE);
                            mBtnUpdate.setEnabled(true);
                            String message;
                            if (responseType == ThemeSelectorViewModel.ResponseType.NoUpdate) {
                                message = getString(R.string.up_to_date);
                            } else {
                                message = getString(R.string.update_success);
                            }
                            Toast.makeText(
                                    ThemeSelectorActivity.this,
                                    message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }, throwable -> {
                            mLoadingLayout.setVisibility(View.GONE);
                            mBtnUpdate.setEnabled(true);
                            Toast.makeText(
                                    ThemeSelectorActivity.this,
                                    R.string.err_no_connect,
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

    @SuppressLint("CheckResult")
    private void onItemClick(int themeId) {
        mViewModel
                .checkWordAvailability(
                        themeId,
                        getGridRowCount(),
                        getGridColCount())
                .subscribe(available -> {
                    if (available) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_THEME_ID, themeId);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(
                                ThemeSelectorActivity.this,
                                "No words data to use, please select another theme or change grid size",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateRevisionNumber() {
        String rev = "-";
        if (mViewModel.getLastDataRevision() > 0)
            rev = String.valueOf(mViewModel.getLastDataRevision());
        mTextRev.setText(rev);
    }

    private int getGridRowCount() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getInt(EXTRA_ROW_COUNT);
        }
        return 0;
    }

    private int getGridColCount() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getInt(EXTRA_COL_COUNT);
        }
        return 0;
    }
}

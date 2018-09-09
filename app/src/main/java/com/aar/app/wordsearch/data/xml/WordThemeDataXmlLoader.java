package com.aar.app.wordsearch.data.xml;

import android.content.Context;
import android.content.res.AssetManager;

import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.model.Word;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class WordThemeDataXmlLoader {

    private static final String ASSET_BASE_FOLDER = "words";

    private AssetManager mAssetManager;

    private List<Word> mWords;
    private List<GameTheme> mGameThemes;

    public WordThemeDataXmlLoader(Context context) {
        mAssetManager = context.getAssets();
    }

    public List<Word> getWords() {
        if (mWords == null) {
            loadData();
        }
        return mWords;
    }

    public List<GameTheme> getGameThemes() {
        if (mGameThemes == null) {
            loadData();
        }
        return mGameThemes;
    }

    public void release() {
        mWords.clear();
        mGameThemes.clear();
        mWords = null;
        mGameThemes = null;
    }

    private void loadData() {
        try {
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SaxWordThemeHandler handler = new SaxWordThemeHandler();
            reader.setContentHandler(handler);

            for (String fileName : getAssetFilePaths()) {
                reader.parse(getInputSource(fileName));
            }

            mWords = handler.getWords();
            mGameThemes = handler.getGameThemes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputSource getInputSource(String fileName) throws IOException {
        return new InputSource(mAssetManager.open(fileName));
    }

    private List<String> getAssetFilePaths() {
        List<String> filePaths = new ArrayList<>();
        try {
            String fileNames[] = mAssetManager.list(ASSET_BASE_FOLDER);
            for (String fileName : fileNames) {
                filePaths.add(ASSET_BASE_FOLDER + "/" + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePaths;
    }
}

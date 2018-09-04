package com.aar.app.wordsearch.data.xml;

import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.model.Word;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SaxWordThemeHandler extends DefaultHandler {

    private static final String XML_WORD_BANK_TAG_NAME = "WordBank";
    private static final String XML_ITEM_TAG_NAME = "item";

    private static final String XML_THEME_NAME_ATTRIBUTE = "theme";
    private static final String XML_STR_ATTRIBUTE = "str";
    private static final String XML_SUB_STR_ATTRIBUTE = "subStr";

    private List<Word> mWords;
    private List<GameTheme> mGameThemes;
    private GameTheme mCurrentGameTheme;

    @Override
    public void startDocument() throws SAXException {
        if (mWords == null) {
            mWords = new ArrayList<>();
        }
        if (mGameThemes == null) {
            mGameThemes = new ArrayList<>();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase(XML_ITEM_TAG_NAME)) {
            int gameThemeId = 0;
            if (mCurrentGameTheme != null) {
                gameThemeId = mCurrentGameTheme.getId();
            }

            Word word = new Word(
                    mWords.size() + 1,
                    gameThemeId,
                    attributes.getValue(XML_STR_ATTRIBUTE),
                    attributes.getValue(XML_SUB_STR_ATTRIBUTE)
            );
            mWords.add(word);
        } else if (qName.equalsIgnoreCase(XML_WORD_BANK_TAG_NAME)) {
            mCurrentGameTheme = new GameTheme(
                    mGameThemes.size() + 1,
                    attributes.getValue(XML_THEME_NAME_ATTRIBUTE)
            );
            mGameThemes.add(mCurrentGameTheme);
        }
    }

    public List<Word> getWords() {
        return mWords;
    }

    public List<GameTheme> getGameThemes() {
        return mGameThemes;
    }
}

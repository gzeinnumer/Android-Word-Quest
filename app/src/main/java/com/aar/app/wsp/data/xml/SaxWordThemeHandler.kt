package com.aar.app.wsp.data.xml

import com.aar.app.wsp.model.GameTheme
import com.aar.app.wsp.model.Word
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.util.*

class SaxWordThemeHandler : DefaultHandler() {
    private var _words: MutableList<Word>? = null
    private var _gameThemes: MutableList<GameTheme>? = null
    private var currentGameTheme: GameTheme? = null

    @Throws(SAXException::class)
    override fun startDocument() {
        if (_words == null) {
            _words = ArrayList()
        }
        if (_gameThemes == null) {
            _gameThemes = ArrayList()
        }
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if (qName.equals(XML_ITEM_TAG_NAME, ignoreCase = true)) {
            var gameThemeId = 0
            if (currentGameTheme != null) {
                gameThemeId = currentGameTheme!!.id
            }
            val word = Word(
                _words!!.size + 1,
                gameThemeId,
                attributes.getValue(XML_STR_ATTRIBUTE)
            )
            _words!!.add(word)
        } else if (qName.equals(XML_WORD_BANK_TAG_NAME, ignoreCase = true)) {
            currentGameTheme = GameTheme(
                _gameThemes!!.size + 1,
                attributes.getValue(XML_THEME_NAME_ATTRIBUTE)
            )
            _gameThemes!!.add(currentGameTheme!!)
        }
    }

    val words: List<Word>?
        get() = _words

    val gameThemes: List<GameTheme>?
        get() = _gameThemes

    companion object {
        private const val XML_WORD_BANK_TAG_NAME = "WordBank"
        private const val XML_ITEM_TAG_NAME = "item"
        private const val XML_THEME_NAME_ATTRIBUTE = "theme"
        private const val XML_STR_ATTRIBUTE = "str"
    }
}
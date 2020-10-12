package com.aar.app.wsp.data.xml

import android.content.Context
import android.content.res.AssetManager
import com.aar.app.wsp.model.GameTheme
import com.aar.app.wsp.model.Word
import org.xml.sax.InputSource
import java.io.IOException
import java.util.*
import javax.xml.parsers.SAXParserFactory

class WordThemeDataXmlLoader(context: Context) {
    private val assetManager: AssetManager = context.assets
    private var _words: List<Word>? = null
    private var _gameThemes: List<GameTheme>? = null

    val words: List<Word>
        get() {
            if (_words == null) {
                loadData()
            }
            return _words ?: emptyList()
        }

    val gameThemes: List<GameTheme>
        get() {
            if (_gameThemes == null) {
                loadData()
            }
            return _gameThemes ?: emptyList()
        }

    fun release() {
        _words = null
        _gameThemes = null
    }

    private fun loadData() {
        try {
            val reader = SAXParserFactory.newInstance().newSAXParser().xmlReader
            val handler = SaxWordThemeHandler()
            reader.contentHandler = handler
            for (fileName in assetFilePaths) {
                reader.parse(getInputSource(fileName))
            }
            _words = handler.words
            _gameThemes = handler.gameThemes
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun getInputSource(fileName: String): InputSource {
        return InputSource(assetManager.open(fileName))
    }

    private val assetFilePaths: List<String>
        get() {
            val filePaths: MutableList<String> = ArrayList()
            try {
                val fileNames = assetManager.list(ASSET_BASE_FOLDER)
                for (fileName in fileNames!!) {
                    filePaths.add("$ASSET_BASE_FOLDER/$fileName")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return filePaths
        }

    companion object {
        private const val ASSET_BASE_FOLDER = "words"
    }

}
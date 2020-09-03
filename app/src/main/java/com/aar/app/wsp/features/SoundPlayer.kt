package com.aar.app.wsp.features

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.util.SparseIntArray
import com.aar.app.wsp.R
import com.aar.app.wsp.features.settings.Preferences
import javax.inject.Inject

/**
 * Created by abdularis on 22/07/17.
 */
class SoundPlayer @Inject constructor(context: Context, private val mPreferences: Preferences) {

    enum class Sound {
        Correct, Wrong, Winning, Lose
    }

    private var soundPool: SoundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
    private val soundPoolMap: SparseIntArray = SparseIntArray()

    fun play(sound: Sound) {
        if (mPreferences.enableSound()) {
            soundPool.play(soundPoolMap[sound.ordinal],
                1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

    init {
        soundPoolMap.put(
            Sound.Correct.ordinal,
            soundPool.load(context, R.raw.correct, 1)
        )
        soundPoolMap.put(
            Sound.Wrong.ordinal,
            soundPool.load(context, R.raw.wrong, 1)
        )
        soundPoolMap.put(
            Sound.Winning.ordinal,
            soundPool.load(context, R.raw.winning, 1)
        )
        soundPoolMap.put(
            Sound.Lose.ordinal,
            soundPool.load(context, R.raw.lose, 1)
        )
    }
}
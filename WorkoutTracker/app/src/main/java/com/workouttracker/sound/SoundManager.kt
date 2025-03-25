package com.workouttracker.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.workouttracker.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val alertSoundId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        alertSoundId = soundPool.load(context, R.raw.alert_sound, 1)
    }

    fun playAlertSound() {
        soundPool.play(alertSoundId, 1f, 1f, 0, 0, 1f)
    }
}

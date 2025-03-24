package org.birdushenin.nadyanyancat

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class RecordStorage(private val settings: Settings) {
    companion object {
        private const val RECORD_KEY = "high_score"
    }

    fun saveRecord(score: Int) {
        settings[RECORD_KEY] = score
    }

    fun getRecord(): Int {
        return settings[RECORD_KEY] ?: 0
    }
}
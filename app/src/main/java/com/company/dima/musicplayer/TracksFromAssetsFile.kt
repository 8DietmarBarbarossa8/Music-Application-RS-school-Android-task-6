package com.company.dima.musicplayer

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.BufferedReader
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class TracksFromAssetsFile @Inject constructor(private val context: Context) : Tracks {

    override val list: List<Track>

    init {
        list = initListOfTrack()
    }

    private fun initListOfTrack(): List<Track> {
        val jsonString = readAsset("playlist.json")
        val listOfTrackType: Type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(jsonString, listOfTrackType)
    }

    private fun readAsset(fileName: String): String {
        return try {
            context
                .assets
                .open(fileName)
                .bufferedReader()
                .use(BufferedReader::readText)
        } catch (e: Exception) { "[]" }
    }
}

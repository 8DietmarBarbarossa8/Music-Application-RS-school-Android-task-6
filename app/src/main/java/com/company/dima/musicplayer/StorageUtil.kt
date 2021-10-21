package com.company.dima.musicplayer

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class StorageUtil(var context: Context) {
    private var preferences: SharedPreferences? = null
    private val STORAGE = " com.fatalzero.android_2021_task_6.STORAGE"

    fun storeAudio(track: Track) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val moshi = Moshi.Builder().build()
        val trackAdapter: JsonAdapter<Track> = moshi.adapter(Track::class.java)
        val editor = preferences?.edit()
        val json: String = trackAdapter.toJson(track)
        editor?.putString("currentTrack", json)
        editor?.apply()
    }

    fun loadIndex(): Int {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preferences?.getInt("index", 0) ?: 0
    }

    fun saveIndex(index: Int) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.putInt("index", index)
        editor?.apply()
    }

    fun loadTrack(): Track? {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val moshi = Moshi.Builder().build()
        val trackAdapter: JsonAdapter<Track> = moshi.adapter(Track::class.java)
        val editor = preferences?.edit()
        val json: String? = preferences?.getString("currentTrack", "")
        return trackAdapter.fromJson(json)
    }

    fun loadAudio(): List<Track>? {

        val moshi = Moshi.Builder().build()

        val file = "playlist.json"
        val listMyData: Type = Types.newParameterizedType(
            MutableList::class.java,
            Track::class.java
        )

        val trackAdapter: JsonAdapter<List<Track>> = moshi.adapter(listMyData)

        val myjson =
            context.resources.openRawResource(R.raw.playlist).bufferedReader().use { it.readText() }

        val list = trackAdapter.fromJson(myjson)
        val oneTrackAdapter: JsonAdapter<Track> = moshi.adapter(Track::class.java)
        val track = oneTrackAdapter.toJson(list?.get(0))
        return list
    }

    fun clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.clear()
        editor?.apply()
    }
}
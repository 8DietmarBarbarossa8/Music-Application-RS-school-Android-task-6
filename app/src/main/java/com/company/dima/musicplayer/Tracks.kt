package com.company.dima.musicplayer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

typealias trackList = List<Track>

@JsonClass(generateAdapter = true)
data class Track (
    @Json(name = "title") val title:String?,
    @Json(name = "artist") val artist:String?,
    @Json(name = "album") val album:String?,
    @Json(name = "bitmapUri") val bitmapUri:String?,
    @Json(name = "trackUri") val trackUri:String?,
    @Json(name = "duration") val duration:Int?
)
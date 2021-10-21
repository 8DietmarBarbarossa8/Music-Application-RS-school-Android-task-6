package com.company.dima.musicplayer

data class AudioInfo(
    var title: String,
    var artist: String,
    var album: String,
    var bitmapUri: String,
    var trackUri: String,
    var duration: Int
)
package com.company.dima.musicplayer

import java.util.concurrent.TimeUnit

interface Tracks {
    val list: List<Track>
}

data class Track(
    val title: String = "",
    val artist: String = "",
    val bitmapUri: String = "",
    val trackUri: String = "",
    val duration: Long = 0
)

fun mscToTime(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)

    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds))

    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))

    return if (hours > 0)
        String.format("%01d:%02d:%02d", hours, minutes, seconds)
    else
        String.format("%01d:%02d", minutes, seconds)
}
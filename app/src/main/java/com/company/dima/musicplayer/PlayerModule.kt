package com.company.dima.musicplayer

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class PlayerModule {
    @Provides
    fun provideTracks(context: Context): MusicPlayer {
        return MusicPlayer(context)
    }
}
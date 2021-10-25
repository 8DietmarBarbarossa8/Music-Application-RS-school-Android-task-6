package com.company.dima.musicplayer

import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {
    @Binds
    abstract fun provideTracks(tracks: TracksFromAssetsFile): Tracks
}
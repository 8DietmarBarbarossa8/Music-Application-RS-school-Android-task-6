package com.company.dima.musicplayer

import android.content.Context
import androidx.lifecycle.ViewModel

import dagger.*
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, PlayerModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
    fun inject(viewModel: ViewModel)
}

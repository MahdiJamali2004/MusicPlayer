package com.mjdev.musicplayer.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mjdev.musicplayer.data.local.DataBase
import com.mjdev.musicplayer.data.music.DeleteMusic
import com.mjdev.musicplayer.data.music.GetMusics
import com.mjdev.musicplayer.data.repository.MainRepositoryImpl
import com.mjdev.musicplayer.data.repository.ThemeRepositoryImpl
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // Create a CoroutineScope tied to the database operations
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): DataBase {
        return DataBase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMainRepository(
        dataBase: DataBase,
        getMusics: GetMusics,
        deleteMusic: DeleteMusic
    ): MainRepository {
        return MainRepositoryImpl(dataBase, getMusics, deleteMusic)
    }

    @Singleton
    @Provides
    fun provideDeleteMusicFromDevice(@ApplicationContext context: Context): DeleteMusic {
        return DeleteMusic(context)
    }

    @Singleton
    @Provides
    fun provideGetMusicFromDevice(@ApplicationContext context: Context): GetMusics {
        return GetMusics(context)
    }

    @Singleton
    @Provides
    fun provideDatastore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    @Singleton
    @Provides
    fun provideThemeRepository(@ApplicationContext context: Context) : ThemeRepository {
        return ThemeRepositoryImpl(context.dataStore)
    }




}
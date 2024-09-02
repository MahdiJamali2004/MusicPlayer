package com.mjdev.musicplayer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme
import com.mjdev.musicplayer.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class  ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {
    private val imageThemeKey = intPreferencesKey(IMAGE_THEME_KEY)
    private val gradientKey = intPreferencesKey(GRADIENT_KEY)
    override suspend fun insertGradient(gradient: Gradient) {
        dataStore.edit {
            it[gradientKey] = gradient.ordinal
        }
    }

    override fun readGradient(): Flow<Int?> {
       return dataStore.data.map {
            it[gradientKey]
        }
    }

    override suspend fun insertImageTheme(imageTheme: ImageTheme) {
        dataStore.edit {
            it[imageThemeKey] = imageTheme.ordinal
        }
    }

    override fun readImageTheme(): Flow<Int?> {
        return dataStore.data.map {
            it[imageThemeKey]
        }
    }
    companion object{
        const val GRADIENT_KEY = "gradientKey"
        const val IMAGE_THEME_KEY = "imageThemeKey"
    }
}
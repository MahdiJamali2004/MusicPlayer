package com.mjdev.musicplayer.data.local.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mjdev.musicplayer.domain.model.InitialMusicItems
import kotlinx.coroutines.flow.Flow

@Dao
interface InitialMusicItemsDao{

    @Upsert
    suspend fun upsertInitialMusicItems(initialMusicItems: InitialMusicItems)


    @Query("SELECT * FROM InitialMusicItems")
    suspend fun getInitialMusicItems() : InitialMusicItems
}
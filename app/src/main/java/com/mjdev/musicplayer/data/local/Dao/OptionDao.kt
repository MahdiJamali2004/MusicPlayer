package com.mjdev.musicplayer.data.local.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mjdev.musicplayer.domain.model.Option
import kotlinx.coroutines.flow.Flow

@Dao
interface OptionDao {

    @Upsert
    suspend fun upsertOption(option: Option)

    @Query("SELECT * FROM OPTION WHERE id =:id")
    fun getOption(id : Int = 1): Flow<Option>


}
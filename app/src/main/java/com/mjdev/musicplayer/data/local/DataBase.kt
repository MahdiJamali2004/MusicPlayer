package com.mjdev.musicplayer.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mjdev.musicplayer.data.local.Dao.InitialMusicItemsDao
import com.mjdev.musicplayer.data.local.Dao.MusicDao
import com.mjdev.musicplayer.data.local.Dao.OptionDao
import com.mjdev.musicplayer.data.local.Dao.PlayListDao
import com.mjdev.musicplayer.data.local.converters.Converters
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.domain.model.InitialMusicItems
import com.mjdev.musicplayer.domain.model.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    [MusicItem::class, PlayList::class, MusicPlayListCrossRef::class, InitialMusicItems::class, Option::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DataBase : RoomDatabase() {

    abstract val musicDao: MusicDao
    abstract val playListDao: PlayListDao
    abstract val optionDao: OptionDao
    abstract val initialMusicItemsDao: InitialMusicItemsDao

    companion object {
        const val DB_NAME = "music.db"

        @Volatile
        var INSTANCE : DataBase? =  null
        fun getDatabase(context: Context) : DataBase{
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context,
                    DataBase::class.java,
                    DB_NAME
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                            INSTANCE?.optionDao?.upsertOption(Option())
                            INSTANCE?.initialMusicItemsDao?.upsertInitialMusicItems(
                                InitialMusicItems()
                            )
                            INSTANCE?.playListDao?.upsertPlayList(PlayList("favorite",System.currentTimeMillis()))
                        }
                    }
                }).build().also {
                    INSTANCE = it
                }

            }
        }
    }

}


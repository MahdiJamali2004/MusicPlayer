package com.mjdev.musicplayer.data.music

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.util.MusicError
import com.mjdev.musicplayer.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class GetMusics @Inject constructor(
    private val context: Context
) {
    suspend operator fun invoke(): Result<List<MusicItem>, MusicError> {
        val musics = mutableListOf<MusicItem>()
       return withContext(Dispatchers.IO) {
            try {
                val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ARTIST,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.YEAR,
                )
                val selection = "${MediaStore.Audio.Media.MIME_TYPE} = ?"
                val selectionArgs = arrayOf("audio/mpeg")
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )?.use { cursor ->

                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val albumIdColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val dateAddedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumArtistColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                    val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                    while (cursor.moveToNext()) {


                        val id = cursor.getLongOrNull(idColumn)
                        val displayName = cursor.getStringOrNull(displayNameColumn)
                        val albumId = cursor.getLongOrNull(albumIdColumn)
                        val artist = cursor.getStringOrNull(artistColumn)
                        val dateAdded = cursor.getLongOrNull(dateAddedColumn)
                        val duration = cursor.getLongOrNull(durationColumn)
                        val album = cursor.getStringOrNull(albumColumn)
                        val albumArtist = cursor.getStringOrNull(albumArtistColumn)
                        val size = cursor.getLongOrNull(sizeColumn)
                        val year = cursor.getLongOrNull(yearColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id!!
                        )
                        val imageUri = getAlbumArtUri(albumId, context)
                        val musicItem = MusicItem(

                            displayName = displayName ?: "Unknown",
                            uri = uri.toString(),
                            imgUri = imageUri,
                            artist = artist ?: "Unknown",
                            albumArtist = albumArtist ?: "Unknown",
                            album = album ?: "Unknown",
                            size = size ?: -1,
                            duration = duration ?: -1,
                            dataAdded = dateAdded ?: -1,
                            year = year ?: -1,
                            musicId = id
                        )

                        musics.add(musicItem)
                    }
                }
                 Result.Success(musics)
            } catch (e: IOException) {
                Log.v("errr","io happend")

                Result.Error(MusicError.IoError)
            } catch (e : Exception){
                Log.v("errr", " unkown happend")

                Result.Error(MusicError.UnknownError)
            }

        }

    }
}

//private fun getLyricsFromMp3(filePath: String): String? {
//    return try {
//        val mp3File = Mp3File(filePath)
//        if (mp3File.hasId3v2Tag()) {
//            val id3v2Tag: ID3v2 = mp3File.id3v2Tag
//            id3v2Tag.lyrics
//        } else {
//            null
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//        Log.v("lyricprob" , e.toString())
//        null
//    }
//}

private fun getAlbumArtUri(albumId: Long?, context: Context): String? {
    return if (albumId != null) {
        val artWorkUri = Uri.parse("content://media/external/audio/albumart")
        val albumArtUri = ContentUris.withAppendedId(artWorkUri, albumId)

        // Check if the URI resolves to a valid content by opening an input stream
        try {
            context.contentResolver.openInputStream(albumArtUri)?.close()
            albumArtUri.toString()
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }

}
/*
private fun getAlbumart(albumId: Long?, context: Context): Bitmap? {
    var bm: Bitmap? = null
    try {
        val artWorkUri = Uri
            .parse("content://media/external/audio/albumart")

        val uri = ContentUris.withAppendedId(artWorkUri, albumId!!)

        val pfd = context.contentResolver
            .openFileDescriptor(uri, "r")

        if (pfd != null) {
            val fd = pfd.fileDescriptor
            bm = BitmapFactory.decodeFileDescriptor(fd)
        }
        pfd?.close()
    } catch (e: Exception) {
    }
    return bm
}*/

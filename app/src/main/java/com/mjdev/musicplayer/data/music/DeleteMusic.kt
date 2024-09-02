package com.mjdev.musicplayer.data.music

import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.util.MusicError
import com.mjdev.musicplayer.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException

class DeleteMusic(
    private val context: Context
) {
    suspend operator fun invoke(musicItems: MusicItem): IntentSender? {
        return withContext(Dispatchers.IO) {
            val uri = Uri.parse(musicItems.uri)
            try {
                context.contentResolver.delete(uri, null, null)
                null
            } catch (e: SecurityException) {
                val intentSender = when{
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(context.contentResolver, mutableListOf(uri)).intentSender
                    }
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                Log.v("deleteError", "$e")
                intentSender
            } catch (e: Exception) {
                Log.v("deleteError", "$e")
                    null
            }
        }
    }
}
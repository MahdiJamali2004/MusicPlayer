package com.mjdev.musicplayer.presentation.mainActivity

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.compose.MusicPlayerTheme
import com.google.common.util.concurrent.MoreExecutors
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.musicService.MusicService
import com.mjdev.musicplayer.presentation.mainActivity.component.ReadAudioPermission
import com.mjdev.musicplayer.presentation.mainnavhost.MainNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mainRepository: MainRepository

    private lateinit var viewModel: MainViewModel
    private var mediaController: MediaController? = null
    private var mediaControllerInit by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        val permission = audioPermission()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ), navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {


            viewModel = hiltViewModel()
            var permissionsList by remember { mutableStateOf(emptyList<String>()) }
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (!isGranted && !permissionsList.contains(permission)) {
                        permissionsList = listOf(permission)
                    }
                    viewModel.onPermissionChange(isGranted)

                })
            MusicPlayerTheme(darkTheme = true) {
                LaunchedEffect(true) {
                    permissionLauncher.launch(permission)
                }
                val states by viewModel.states.collectAsState()
                if (mediaControllerInit && viewModel.isPermissionGranted.collectAsState().value) {

                    if (!mediaController!!.isPlaying && mediaController!!.currentMediaItem == null && states.mediaItems.isNotEmpty()) {
                        mediaController!!.setMediaItems(
                            states.mediaItems,
                            states.startItem,
                            states.startPosition
                        )
//                        mediaController!!.addMediaItems(states.mediaItems)
//                        mediaController!!.seekTo(states.startItem,states.startPosition)
                        mediaController!!.prepare()

                    }

                    MainNavHost(
                        mediaController = mediaController!!,
                        settedMusics = states.mediaItems
                    )


                }

                permissionsList.forEach {
                    if (permissionsList.contains(permission)) {
                        ReadAudioPermission(
                            onOpenSetting = ::openDetailSetting,
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                            onDismissRequest = { permissionsList = emptyList() },
                            onConfirmClick = { permissionLauncher.launch(permission) }
                        )
                    }
                }


            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, MusicService::class.java))
        val controllerFuture =
            MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            mediaControllerInit = true
        }, MoreExecutors.directExecutor())


    }


    override fun onStop() {
        super.onStop()
        if (!mediaController!!.isPlaying) {
            viewModel.saveMediaItems(mediaController!!)


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaController!!.release()
    }

    private fun openDetailSetting() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also {
            startActivity(it)
        }
    }

    override fun onRestart() {
        super.onRestart()
        val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        viewModel.onPermissionChange(isGranted)
        if (!isGranted) {
            finish()
        }

    }

    private fun audioPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    }


}



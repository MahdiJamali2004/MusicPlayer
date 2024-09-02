package com.mjdev.musicplayer.domain.util

sealed interface Error
enum class MusicError : Error{
    IoError,
    UnknownError
}
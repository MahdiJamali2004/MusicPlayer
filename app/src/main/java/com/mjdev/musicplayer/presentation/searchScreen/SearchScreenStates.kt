package com.mjdev.musicplayer.presentation.searchScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList

data class SearchScreenStates(
    val musics : List<MusicItem> = emptyList(),
    val filteredMusics : List<MusicItem> = emptyList(),
    val query : String = "",
    val isHintVisible : Boolean = true,
    val option: Option = Option(),
    val toolMediaItemIndex : Int = 0,
    val toolClickPlaylists : List<PlayList> = emptyList(),
    val allPlaylists : List<PlayList> = emptyList(),
    val deletedMusicUri : MusicItem? = null,

)

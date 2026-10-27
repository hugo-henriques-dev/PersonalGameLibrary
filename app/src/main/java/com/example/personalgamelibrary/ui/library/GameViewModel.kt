package com.example.personalgamelibrary.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.personalgamelibrary.GameLibraryApplication
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.data.VideoGameDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModel(private val videoGameDao: VideoGameDao) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val games: StateFlow<List<VideoGame>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                videoGameDao.getAll()
            } else {
                videoGameDao.searchByName(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addGame(game: VideoGame) {
        viewModelScope.launch(Dispatchers.IO) {
            videoGameDao.insert(game)
        }
    }

    fun updateGame(game: VideoGame) {
        viewModelScope.launch(Dispatchers.IO) {
            videoGameDao.update(game)
        }
    }

    fun deleteGame(game: VideoGame) {
        viewModelScope.launch(Dispatchers.IO) {
            videoGameDao.delete(game)
        }
    }

    class Factory(private val application: GameLibraryApplication) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                return GameViewModel(application.database.videoGameDao()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

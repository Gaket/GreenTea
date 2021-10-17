package ru.gaket.themoviedb.screens.movies.mvvm

sealed class SearchState
object Loading : SearchState()
object Ready : SearchState()

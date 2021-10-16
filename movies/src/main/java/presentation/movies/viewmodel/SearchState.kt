package ru.gaket.themoviedb.ru.gaket.themoviedb.presentation.movies.viewmodel

sealed class SearchState
object Loading : SearchState()
object Ready : SearchState()
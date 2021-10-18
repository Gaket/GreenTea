package ru.gaket.themoviedb.screens.movies.demo

object MoviesFeature {

  data class State(
    val a : Boolean
  )

  sealed class Message {
  }

//  object Logic {
//
//    fun update(message: Message, state: State): Update<State, Message, Dependencies> =
//  }

  object Commands {
  }

  class Dependencies(
  )

}

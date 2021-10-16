package ru.gaket.themoviedb.model.movies.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Movies api of themoviedb.org
 */
interface MoviesApi {

  @GET("search/movie")
  suspend fun searchMovie(
      @Query("api_key") apiKey: String,
      @Query("query") query: String,
      @Query("page") page: Int = 1
  ): SearchMovieResponse
}
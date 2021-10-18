package ru.gaket.themoviedb.data.network

import com.google.gson.annotations.SerializedName

/**
 * Class of Movies coming from the api
 */
data class MovieNetworkModel(

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String
)

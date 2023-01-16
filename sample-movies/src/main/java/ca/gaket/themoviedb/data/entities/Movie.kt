package ca.gaket.themoviedb.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Business class of Movies
 */
@Parcelize
data class Movie(val id: Int, val name: String, val thumbnail: String?) : Parcelable

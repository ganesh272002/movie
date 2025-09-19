package com.example.movieseries.data


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Movie(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("overview") val overview: String,
    var isSaved: Boolean = false
): Parcelable

@Parcelize
data class Series(
    val id: Int,
    val name: String?, // series title
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("overview") val overview: String,
    val isSaved: Boolean = false
) : Parcelable



data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)


data class SeriesResponse(
    val page: Int,
    val results: List<Series>,
    val total_pages: Int,
    val total_results: Int
)


data class MovieCategory(
    val title: String,
    val movies: List<Movie>
)

data class SeriesCategory(
    val title: String,
    val series: List<Series>
)

data class PaginationState(
    var currentPage: Int = 1,
    var totalPages: Int = 1,
    var isLoading: Boolean = false,
    var isLastPage: Boolean = false
)





@Parcelize
@Entity(tableName = "saved_items")
data class SavedItemEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val rating: Double?,
    val type: String
): Parcelable
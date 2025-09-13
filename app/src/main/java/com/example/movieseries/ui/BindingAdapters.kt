package com.example.movieseries.ui




import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.movieseries.R

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load("https://image.tmdb.org/t/p/w200" + url)
            .into(view)
    } else {
        view.setImageDrawable(null) // or a placeholder
    }
}

@BindingAdapter("heartIcon")
fun setHeartIcon(view: ImageView, isSaved: Boolean) {
    val iconRes = if (isSaved) {
        R.drawable.filled_heart   // your filled heart drawable
    } else {
        R.drawable.not_filled_heart  // your outline heart drawable
    }
    view.setImageResource(iconRes)
}


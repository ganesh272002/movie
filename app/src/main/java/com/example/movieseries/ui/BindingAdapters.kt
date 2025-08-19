package com.example.movieseries.ui



import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

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

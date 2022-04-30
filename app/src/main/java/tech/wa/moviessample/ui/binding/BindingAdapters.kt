package tech.wa.moviessample.ui.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.wa.moviessample.R

@BindingAdapter("app:url")
fun provideImageUrlLoader(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView)
            .load(it)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .error(R.drawable.ic_loading_error)
            .placeholder(R.drawable.ic_retry)
            .into(imageView)
    }
}
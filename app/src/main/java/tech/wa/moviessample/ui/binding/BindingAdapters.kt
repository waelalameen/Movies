package tech.wa.moviessample.ui.binding

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.wa.moviessample.R

@BindingAdapter("app:url")
fun provideImageUrlLoader(imageView: ImageView, url: String?) {
    url?.let {
        val context = imageView.context

        val circularProgressDrawable = CircularProgressDrawable(imageView.context)
        circularProgressDrawable.centerRadius = 32f
        circularProgressDrawable.strokeWidth = 8f
        circularProgressDrawable.setColorSchemeColors(context.getColor(R.color.salmon))
        circularProgressDrawable.start()

        Glide.with(imageView)
            .load(it)
            .centerInside()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .error(circularProgressDrawable)
            .placeholder(circularProgressDrawable)
            .into(imageView)
    }
}

@BindingAdapter("app:Illustration")
fun provideIllustrationDrawableLoader(imageView: ImageView, drawableId: Int) {
    val context = imageView.context
    imageView.setImageDrawable(AppCompatResources.getDrawable(context, drawableId))
}
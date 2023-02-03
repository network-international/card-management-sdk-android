package ae.network.nicardmanagementsdk.presentation.binding_adapters

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.presentation.extension_methods.getThemeColor
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter


@BindingAdapter("setImageDrawableCustom")
fun setImageDrawableCustom(imageView: ImageView, drawable: Drawable) {
    imageView.setImageDrawable(drawable)
}

@BindingAdapter("tint")
fun setImageTint(imageView: ImageView, @ColorInt color: Int) {
    imageView.setColorFilter(color)
}

@BindingAdapter("useDisabledColor")
fun setImageViewDisabledColor(imageButton: ImageButton, isDisabled: Boolean) {
    val disabledColor = imageButton.context.getThemeColor(R.attr.colorDisabledImageButton)
    val enabledColor = ContextCompat.getColor(imageButton.context, android.R.color.white)
    imageButton.setColorFilter(if (isDisabled) disabledColor else enabledColor)
}

@BindingAdapter("useDisabledBgTint")
fun setImageViewDisabledBgTint(imageButton: ImageButton, isDisabled: Boolean) {
    val disabledColor = ContextCompat.getColor(imageButton.context, android.R.color.transparent)
    val enabledColor = ContextCompat.getColor(imageButton.context, R.color.green1)
    imageButton.backgroundTintList = ColorStateList.valueOf(
        if (isDisabled) disabledColor else enabledColor
    )
}



package ae.network.nicardmanagementsdk.presentation.extension_methods

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import java.io.Serializable

@ColorInt
fun Context.getThemeColor(
    @AttrRes themeAttrId: Int
): Int {
    return theme.obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).run {
        try {
            getColor(0, Color.WHITE)
        } finally {
            recycle()
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Intent.getSerializableExtraCompat(key: String): T? {
    return getSerializableExtra(key)?.let { it as T }

}

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Bundle.getSerializableCompat(key: String): T? {
    return getSerializable(key)?.let { it as T }
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
}
fun TextView.setColorRes(@ColorRes colorRes: Int) {
    this.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
}

fun ImageView.setTint3(@ColorInt color: Int?) {
    if (color == null) {
        ImageViewCompat.setImageTintList(this, null)
        return
    }
    ImageViewCompat.setImageTintMode(this, PorterDuff.Mode.SRC_ATOP)
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}
fun ImageView.setTint2(context: Context, @ColorRes colorId: Int) {
    val color = ContextCompat.getColor(context, colorId)
    val colorStateList = ColorStateList.valueOf(color)
    ImageViewCompat.setImageTintList(this, colorStateList)
}



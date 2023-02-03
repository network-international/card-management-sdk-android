package ae.network.nicardmanagementsdk.presentation.extension_methods

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
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
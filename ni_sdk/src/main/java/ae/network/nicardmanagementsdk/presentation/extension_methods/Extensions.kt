package ae.network.nicardmanagementsdk.presentation.extension_methods

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
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

fun <T : Serializable> Intent.getSerializableExtraCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, clazz)
    } else {
        getSerializableExtra(key)?.let { it as T }
    }
}

fun <T : Serializable> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz)
    } else {
        getSerializable(key)?.let { it as T }
    }
}
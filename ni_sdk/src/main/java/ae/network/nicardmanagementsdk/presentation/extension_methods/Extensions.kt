package ae.network.nicardmanagementsdk.presentation.extension_methods

import ae.network.nicardmanagementsdk.api.models.input.CardElementLayout
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.helpers.ConnectConstraint
import ae.network.nicardmanagementsdk.helpers.ConstraintInstructions
import ae.network.nicardmanagementsdk.helpers.DisconnectConstraint
import ae.network.nicardmanagementsdk.helpers.updateConstraints
import ae.network.nicardmanagementsdk.presentation.views.ShimmerView
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
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

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun View.setConstraints(position: CardElementLayout, constraintLayout: ConstraintLayout) {
    if (position.left == null && position.right == null && position.top == null && position.bottom == null) {
        return
    }
    val viewId = this.id
    constraintLayout.updateConstraints(
        listOf(
        // By default every view has top-left constraints - clear it
        DisconnectConstraint(viewId, ConstraintSet.START), DisconnectConstraint(viewId, ConstraintSet.TOP)
    )
    )

    val instructions: MutableList<ConstraintInstructions> = mutableListOf()

    position.left?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START))
        this.updatePadding(left = it.dp)
    }
    position.top?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP))
        this.updatePadding(top = it.dp)
    }
    position.bottom?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM))
        this.updatePadding(top = it.dp)
    }
    position.right?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END))
        //this.setMargins(right = it)
        this.updatePadding(right = it.dp)
    }
    constraintLayout.updateConstraints(instructions)
}

private fun View.setMargins(
    left: Int = this.marginLeft,
    top: Int = this.marginTop,
    right: Int = this.marginRight,
    bottom: Int = this.marginBottom,
) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(left, top, right, bottom)
    }
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

fun ShimmerView.setSize(imageView: ImageView) {
    if (imageView.drawable == null || imageView.alpha == 0f || imageView.visibility == View.INVISIBLE) {
        return
    }
    val layoutParams = this.layoutParams
    layoutParams.width = imageView.drawable.intrinsicWidth
    layoutParams.height = imageView.drawable.intrinsicHeight
    this.layoutParams = layoutParams
}
fun ShimmerView.setSize(textView: TextView, sampleIfEmpty: String) {
    val layoutParams = this.layoutParams
    var changed = false
    if (textView.text.isEmpty()) {
        textView.text = sampleIfEmpty
        changed = true
    }
    textView.measure(0, 0)
    layoutParams.height = textView.measuredHeight
    layoutParams.width = textView.measuredWidth
    if (changed) {
        textView.text = ""
    }
    this.layoutParams = layoutParams
}

//fun TextView.setFont(context: Context, uiFont: UIFont?) {
//    uiFont?.let {
//        this.apply {
//            uiFont.fontRes?.let {
//                typeface = ResourcesCompat.getFont(context, it)
//            }
//            textSize = uiFont.textSize.toFloat()
//        }
//    }
//}

fun ImageView.setContentDescrText(data: UIElementText) {
    when(data) {
        is UIElementText.Int -> this.context.getString(data.value)
        is UIElementText.String -> this.contentDescription = data.value
    }
}

fun TextView.setUIElementText(data: UIElementText) {
    when(data) {
        is UIElementText.Int -> this.setText(data.value)
        is UIElementText.String -> this.text = data.value
    }
}

fun TextView.setUIElementText(data: UIElementText, vararg args: Any?) {
    // resources.getString(R.string.get_pin_countdown_timer_text_ar, time)
    when(data) {
        is UIElementText.Int -> this.text = this.resources.getString(data.value, args)
        is UIElementText.String -> this.text = String.format(data.value, args)
    }
}
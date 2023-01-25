package ae.network.nicardmanagementsdk.presentation.views

import ae.network.nicardmanagementsdk.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes

class CustomBackNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

     private val imageButton: ImageButton
     private val textView: TextView
     private var onClickListener : (() -> Unit)? = null

    init {
        inflate(context, R.layout.custom_back_navigation_view, this)
        imageButton = findViewById(R.id.imageButton)
        textView = findViewById(R.id.textView)

        var iconDrawable: Drawable? = null
        var title: String? = null
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomBackNavigationView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                iconDrawable = getDrawable(R.styleable.CustomBackNavigationView_icon)
                title = getString(R.styleable.CustomBackNavigationView_title)
            } finally {
                recycle()
            }
        }
        iconDrawable?.let { imageButton.setImageDrawable(it) }
        title?.let { textView.text = it }

        imageButton.setOnClickListener {
            onClickListener?.let { onClick -> onClick() }
        }
    }

    fun setOnBackButtonClickListener(onClick: () -> Unit) {
        onClickListener = onClick
    }

    fun setTitle(message: String) {
        textView.text = message
    }

    fun setIcon(@DrawableRes resId: Int) {
        imageButton.setImageResource(resId)
    }
}
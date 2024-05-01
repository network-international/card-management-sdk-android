package ae.network.nicardmanagementsdk.presentation.views

import ae.network.nicardmanagementsdk.R
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout

class ShimmerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val shimmerHStack: View
    private val shimmerHolder: View
    //private val gradientDrawable: GradientDrawable

    init {
        inflate(context, R.layout.shimmer_view, this)
        shimmerHStack = findViewById(R.id.shimmerHStack)
        shimmerHolder = findViewById(R.id.shimmerHolder)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val gradientDrawable = shimmerHolder.background as GradientDrawable
        gradientDrawable.cornerRadius = (h/2).toFloat()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (this.alpha == 0f) {
            return
        }
        if (visibility == View.VISIBLE) {
            startShimmering()
        } else {
            //stopShimmering()
        }
    }
    fun startShimmering() {
        //val w = width
        val anim = TranslateAnimation(-500f, 500f, 0f, 0f )
        anim.duration = 660
        anim.repeatCount = Animation.INFINITE
        anim.interpolator = LinearInterpolator()
        //anim.fillBefore = true
//        //animation.setRepeatMode(Animation.REVERSE);
        //gradientBackground.start()
        shimmerHStack.startAnimation(anim)
    }

    fun stopShimmering() {
        shimmerHStack.clearAnimation()
    }
}

class GradientAnimationDrawable(
    start: Int = Color.parseColor("#E6D3D3D3"),
    center: Int = Color.parseColor("#00D3D3D3"),
    end: Int = Color.parseColor("#E6D3D3D3"),
    frameDuration: Int = 660/3,
    enterFadeDuration: Int = 660,
    exitFadeDuration: Int = 660
) : AnimationDrawable() {
    private var gradientStart = GradientDrawable(GradientDrawable.Orientation.BL_TR, intArrayOf(start, center, end))
        .apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
    private var gradientCenter = GradientDrawable(GradientDrawable.Orientation.BL_TR, intArrayOf(end, start, center))
        .apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
    private var gradientEnd = GradientDrawable(GradientDrawable.Orientation.BL_TR, intArrayOf(center, end, start))
        .apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

    init {
        addFrame(gradientStart, frameDuration)
        addFrame(gradientCenter, frameDuration)
        addFrame(gradientEnd, frameDuration)

        setEnterFadeDuration(enterFadeDuration)
        setExitFadeDuration(exitFadeDuration)
        isOneShot = false
    }
}
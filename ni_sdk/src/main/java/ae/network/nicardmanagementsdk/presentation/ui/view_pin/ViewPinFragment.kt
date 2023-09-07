package ae.network.nicardmanagementsdk.presentation.ui.view_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.databinding.FragmentViewPinBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


abstract class ViewPinFragment : Fragment() {
    protected var listener: OnFragmentInteractionListener? = null
    private var successErrorResponse: SuccessErrorResponse? = null
    private lateinit var viewModel: ViewPinFragmentViewModel

    private lateinit var niInput: NIInput
    private var _pinViewBinding: FragmentViewPinBinding? = null
    private val pinViewBinding: FragmentViewPinBinding
        get() = _pinViewBinding!!
    private var startTime: Long? = null
    private var strokeColor: String? = null
    private lateinit var timer: CountDownTimer

    abstract fun checkSubscriber(context: Context)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkSubscriber(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializableCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        }
            ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_INPUT} is missing")

        arguments?.getLong(Extra.EXTRA_VIEW_PIN_START_TIME)?.let {
            startTime = it
        }
            ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_VIEW_PIN_START_TIME} is missing")

        arguments?.getString(Extra.EXTRA_VIEW_PIN_STROKE_COLOR)?.let {
            strokeColor = it
        }
            ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_VIEW_PIN_STROKE_COLOR} is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory =
            Injector.getInstance(requireContext()).provideViewPinFragmentViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[ViewPinFragmentViewModel::class.java]
        _pinViewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_view_pin, container, false)
        pinViewBinding.lifecycleOwner = this
        pinViewBinding.viewModel = viewModel
        return pinViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()
    }

    private fun setTimerText(time: Int): String {
        return if (LanguageHelper().getLanguage(niInput) == "ar")
            resources.getString(R.string.get_pin_countdown_timer_text_ar, time)
        else resources.getString(R.string.get_pin_countdown_timer_text_en, time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _pinViewBinding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initializeUI() {
        viewModel.getPinClearLiveData.observe(viewLifecycleOwner) { pinClear ->
            if (pinClear != null) {
                val pinLength = pinClear.length
                setPinView(pinLength)
                setPinClear(pinLength)

                timer = object : CountDownTimer(startTime!!, COUNTDOWN_INTERVAL) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (pinViewBinding != null) {
                            pinViewBinding.countdownTimerTextView.visibility = View.VISIBLE
                            val secondsToInt = (millisUntilFinished / 1000).toInt()
                            pinViewBinding.countdownTimerTextView.text =
                                setTimerText(secondsToInt)
                        }
                    }

                    override fun onFinish() {
                        setPinMasked(pinLength)
                        pinViewBinding.countdownTimerTextView.visibility = View.INVISIBLE
                    }
                }.start()
            }
        }
        if (viewModel.getPinClearLiveData.value == null) {
            viewModel.getPin()
        }
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    private fun setPinView(pinLength: Int) {
        when (pinLength) {
            5 -> {
                pinViewBinding.defaultPinView.root.visibility = View.GONE
                pinViewBinding.fiveDigitPinView.root.visibility = View.VISIBLE
                pinViewBinding.fiveDigitPinView.pinFirstDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.pinSecondDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.pinThirdDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.pinForthDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.pinFifthDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.view.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.view2.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.view3.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.view4.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.mainContent.background =
                    buildViewPinBackground(strokeColor!!)
            }
            6 -> {
                pinViewBinding.defaultPinView.root.visibility = View.GONE
                pinViewBinding.sixDigitPinView.root.visibility = View.VISIBLE
                pinViewBinding.sixDigitPinView.pinFirstDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.pinSecondDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.pinThirdDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.pinForthDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.pinFifthDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.pinSixthDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.view.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.view2.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.view3.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.view4.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.sixDigitPinView.view5.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fiveDigitPinView.mainContent.background =
                    buildViewPinBackground(strokeColor!!)
            }
            else -> {
                pinViewBinding.defaultPinView.root.visibility = View.GONE
                pinViewBinding.fourDigitPinView.root.visibility = View.VISIBLE
                pinViewBinding.fourDigitPinView.pinFirstDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fourDigitPinView.pinSecondDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fourDigitPinView.pinThirdDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fourDigitPinView.pinForthDigit.setTextColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fourDigitPinView.view.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fourDigitPinView.view2.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fourDigitPinView.view3.setBackgroundColor(
                    Color.parseColor(
                        strokeColor
                    )
                )
                pinViewBinding.fourDigitPinView.mainContent.background =
                    buildViewPinBackground(strokeColor!!)
            }
        }
        pinViewBinding.countdownTimerTextView.setTextColor(Color.parseColor(strokeColor))
    }

    private fun setPinClear(pinLength: Int) {
        when (pinLength) {
            5 -> {
                pinViewBinding.fiveDigitPinView.pinFirstDigit.text =
                    viewModel.getPinClearLiveData.value?.get(0).toString()
                pinViewBinding.fiveDigitPinView.pinSecondDigit.text =
                    viewModel.getPinClearLiveData.value?.get(1).toString()
                pinViewBinding.fiveDigitPinView.pinThirdDigit.text =
                    viewModel.getPinClearLiveData.value?.get(2).toString()
                pinViewBinding.fiveDigitPinView.pinForthDigit.text =
                    viewModel.getPinClearLiveData.value?.get(3).toString()
                pinViewBinding.fiveDigitPinView.pinFifthDigit.text =
                    viewModel.getPinClearLiveData.value?.get(4).toString()
            }
            6 -> {
                pinViewBinding.sixDigitPinView.pinFirstDigit.text =
                    viewModel.getPinClearLiveData.value?.get(0).toString()
                pinViewBinding.sixDigitPinView.pinSecondDigit.text =
                    viewModel.getPinClearLiveData.value?.get(1).toString()
                pinViewBinding.sixDigitPinView.pinThirdDigit.text =
                    viewModel.getPinClearLiveData.value?.get(2).toString()
                pinViewBinding.sixDigitPinView.pinForthDigit.text =
                    viewModel.getPinClearLiveData.value?.get(3).toString()
                pinViewBinding.sixDigitPinView.pinFifthDigit.text =
                    viewModel.getPinClearLiveData.value?.get(4).toString()
                pinViewBinding.sixDigitPinView.pinSixthDigit.text =
                    viewModel.getPinClearLiveData.value?.get(5).toString()
            }
            else -> {
                pinViewBinding.fourDigitPinView.pinFirstDigit.text =
                    viewModel.getPinClearLiveData.value?.get(0).toString()
                pinViewBinding.fourDigitPinView.pinSecondDigit.text =
                    viewModel.getPinClearLiveData.value?.get(1).toString()
                pinViewBinding.fourDigitPinView.pinThirdDigit.text =
                    viewModel.getPinClearLiveData.value?.get(2).toString()
                pinViewBinding.fourDigitPinView.pinForthDigit.text =
                    viewModel.getPinClearLiveData.value?.get(3).toString()
            }
        }
    }


    private fun setPinMasked(pinLength: Int) {
        when (pinLength) {
            5 -> {
                pinViewBinding.fiveDigitPinView.pinFirstDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(0).toString()
                pinViewBinding.fiveDigitPinView.pinSecondDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(1).toString()
                pinViewBinding.fiveDigitPinView.pinThirdDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(2).toString()
                pinViewBinding.fiveDigitPinView.pinForthDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(3).toString()
                pinViewBinding.fiveDigitPinView.pinFifthDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(4).toString()
            }
            6 -> {
                pinViewBinding.sixDigitPinView.pinFirstDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(0).toString()
                pinViewBinding.sixDigitPinView.pinSecondDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(1).toString()
                pinViewBinding.sixDigitPinView.pinThirdDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(2).toString()
                pinViewBinding.sixDigitPinView.pinForthDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(3).toString()
                pinViewBinding.sixDigitPinView.pinFifthDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(4).toString()
                pinViewBinding.sixDigitPinView.pinSixthDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(5).toString()
            }
            else -> {
                pinViewBinding.fourDigitPinView.pinFirstDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(0).toString()
                pinViewBinding.fourDigitPinView.pinSecondDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(1).toString()
                pinViewBinding.fourDigitPinView.pinThirdDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(2).toString()
                pinViewBinding.fourDigitPinView.pinForthDigit.text =
                    viewModel.getPinMaskedLiveData.value?.get(3).toString()
            }
        }
    }

    private fun buildViewPinBackground(color: String): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(Color.parseColor(BLACK))
        drawable.setStroke(5, Color.parseColor(color))
        val paddingRect = Rect(5, 5, 5, 5)
        drawable.getPadding(paddingRect)
        drawable.cornerRadii = floatArrayOf(40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f)
        return drawable
    }

    companion object {
        const val COUNTDOWN_INTERVAL = 1000L
        const val BLACK = "#00000000"
    }

    interface OnFragmentInteractionListener {
        fun onViewPinFragmentCompletion(response: SuccessErrorResponse)
    }
}
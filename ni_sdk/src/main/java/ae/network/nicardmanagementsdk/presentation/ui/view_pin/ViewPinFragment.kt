package ae.network.nicardmanagementsdk.presentation.ui.view_pin

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.PinManagementResources
import ae.network.nicardmanagementsdk.databinding.FragmentViewPinBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.extension_methods.setUIElementText
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class ViewPinFragment : Fragment() {
    protected var listener: OnFragmentInteractionListener? = null
    private var successErrorResponse: SuccessErrorResponse? = null
    private lateinit var viewModel: ViewPinFragmentViewModel

    private lateinit var niInput: NIInput
    private lateinit var texts: PinManagementResources
    private var _binding: FragmentViewPinBinding? = null
    private val binding get() = _binding!!
    private var startTime: Long? = null
    private var strokeColor: String? = null
    private lateinit var timer: CountDownTimer

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, texts: PinManagementResources, timer: Long = 6000L, color: String = BLACK) = ViewPinFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_VIEW_PIN_START_TIME, timer)
                putSerializable(Extra.EXTRA_VIEW_PIN_STROKE_COLOR, color)

                putSerializable(Extra.EXTRA_NI_PIN_FORM_RESOURCES, texts)
                // Fragment
                //putSerializable(Extra.EXTRA_NI_INPUT, input)
                //putSerializable(Extra.EXTRA_NI_PIN_FORM_TYPE, type)   type: NIPinFormType
            }
        }

        const val TAG = "ViewPinFragment"
        private const val BLACK = "#FF000000"
        const val COUNTDOWN_INTERVAL = 1000L

    }

    private fun checkSubscriber(context: Context) {
        listener = if (context is OnFragmentInteractionListener) {
            context
        } else if (parentFragment is OnFragmentInteractionListener) {
            parentFragment as OnFragmentInteractionListener
        } else {
            throw RuntimeException("Must implement ViewPinFragment.OnFragmentInteractionListener")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkSubscriber(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializableCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_INPUT} is missing")

        arguments?.getSerializableCompat<PinManagementResources>(Extra.EXTRA_NI_PIN_FORM_RESOURCES)?.let {
            texts = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_PIN_FORM_RESOURCES} is missing")

        arguments?.getLong(Extra.EXTRA_VIEW_PIN_START_TIME)?.let {
            startTime = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_VIEW_PIN_START_TIME} is missing")

        arguments?.getString(Extra.EXTRA_VIEW_PIN_STROKE_COLOR)?.let {
            strokeColor = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_VIEW_PIN_STROKE_COLOR} is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val timerTemplate = texts.viewPin.timerTemplate
        val factory =
            Injector.getInstance(requireContext()).provideViewPinFragmentViewModelFactory(niInput, timerTemplate)
        viewModel = ViewModelProvider(this, factory)[ViewPinFragmentViewModel::class.java]
        _binding = FragmentViewPinBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    private fun initializeUI() {
        viewModel.getPinClearLiveData.observe(viewLifecycleOwner) { pinClear ->
            if (pinClear != null) {
                val pinLength = pinClear.length
                setPinView(pinLength)
                setPinClear(pinLength)

                timer = object : CountDownTimer(startTime!!, COUNTDOWN_INTERVAL) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.countdownTimerTextView.visibility = View.VISIBLE
                        val secondsToInt = (millisUntilFinished / 1000).toInt()
                        binding.countdownTimerTextView.setUIElementText(viewModel.timerStringTemplate, secondsToInt)
                    }

                    override fun onFinish() {
                        setPinMasked(pinLength)
                        binding.countdownTimerTextView.visibility = View.INVISIBLE
                    }
                }.start()
            }
        }

        viewModel.isVisibleProgressBar.observe(viewLifecycleOwner) { isVisible ->
            binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        }
        if (viewModel.getPinClearLiveData.value == null) {
            viewModel.getPin()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Set PIN view visibility based on PIN length
     */
    private fun setPinView(pinLength: Int) {
        when (pinLength) {
            5 -> {
                binding.defaultPinView.root.visibility = View.GONE
                binding.fiveDigitPinView.root.visibility = View.VISIBLE
                setupFiveDigitView()
            }
            6 -> {
                binding.defaultPinView.root.visibility = View.GONE
                binding.sixDigitPinView.root.visibility = View.VISIBLE
                setupSixDigitView()
            }
            else -> {
                binding.defaultPinView.root.visibility = View.GONE
                binding.fourDigitPinView.root.visibility = View.VISIBLE
                setupFourDigitView()
            }
        }
        binding.countdownTimerTextView.setTextColor(Color.parseColor(strokeColor))
    }

    /**
     * Setup styling for 4-digit PIN view
     */
    private fun setupFourDigitView() {
        val color = Color.parseColor(strokeColor)
        binding.fourDigitPinView.apply {
            pinFirstDigit.setTextColor(color)
            pinSecondDigit.setTextColor(color)
            pinThirdDigit.setTextColor(color)
            pinForthDigit.setTextColor(color)

            view.setBackgroundColor(color)
            view2.setBackgroundColor(color)
            view3.setBackgroundColor(color)

            mainContent.background = buildViewPinBackground(strokeColor!!)
        }
    }

    /**
     * Setup styling for 5-digit PIN view
     */
    private fun setupFiveDigitView() {
        val color = Color.parseColor(strokeColor)
        binding.fiveDigitPinView.apply {
            pinFirstDigit.setTextColor(color)
            pinSecondDigit.setTextColor(color)
            pinThirdDigit.setTextColor(color)
            pinForthDigit.setTextColor(color)
            pinFifthDigit.setTextColor(color)

            view.setBackgroundColor(color)
            view2.setBackgroundColor(color)
            view3.setBackgroundColor(color)
            view4.setBackgroundColor(color)

            mainContent.background = buildViewPinBackground(strokeColor!!)
        }
    }

    /**
     * Setup styling for 6-digit PIN view
     */
    private fun setupSixDigitView() {
        val color = Color.parseColor(strokeColor)
        binding.sixDigitPinView.apply {
            pinFirstDigit.setTextColor(color)
            pinSecondDigit.setTextColor(color)
            pinThirdDigit.setTextColor(color)
            pinForthDigit.setTextColor(color)
            pinFifthDigit.setTextColor(color)
            pinSixthDigit.setTextColor(color)

            view.setBackgroundColor(color)
            view2.setBackgroundColor(color)
            view3.setBackgroundColor(color)
            view4.setBackgroundColor(color)
            view5.setBackgroundColor(color)

            mainContent.background = buildViewPinBackground(strokeColor!!)
        }
    }

    /**
     * Display clear PIN digits
     */
    private fun setPinClear(pinLength: Int) {
        val pinValue = viewModel.getPinClearLiveData.value ?: return

        when (pinLength) {
            5 -> {
                binding.fiveDigitPinView.apply {
                    pinFirstDigit.text = pinValue.getOrNull(0)?.toString() ?: ""
                    pinSecondDigit.text = pinValue.getOrNull(1)?.toString() ?: ""
                    pinThirdDigit.text = pinValue.getOrNull(2)?.toString() ?: ""
                    pinForthDigit.text = pinValue.getOrNull(3)?.toString() ?: ""
                    pinFifthDigit.text = pinValue.getOrNull(4)?.toString() ?: ""
                }
            }
            6 -> {
                binding.sixDigitPinView.apply {
                    pinFirstDigit.text = pinValue.getOrNull(0)?.toString() ?: ""
                    pinSecondDigit.text = pinValue.getOrNull(1)?.toString() ?: ""
                    pinThirdDigit.text = pinValue.getOrNull(2)?.toString() ?: ""
                    pinForthDigit.text = pinValue.getOrNull(3)?.toString() ?: ""
                    pinFifthDigit.text = pinValue.getOrNull(4)?.toString() ?: ""
                    pinSixthDigit.text = pinValue.getOrNull(5)?.toString() ?: ""
                }
            }
            else -> {
                binding.fourDigitPinView.apply {
                    pinFirstDigit.text = pinValue.getOrNull(0)?.toString() ?: ""
                    pinSecondDigit.text = pinValue.getOrNull(1)?.toString() ?: ""
                    pinThirdDigit.text = pinValue.getOrNull(2)?.toString() ?: ""
                    pinForthDigit.text = pinValue.getOrNull(3)?.toString() ?: ""
                }
            }
        }
    }


    /**
     * Display masked PIN digits
     */
    private fun setPinMasked(pinLength: Int) {
        val pinValue = viewModel.getPinMaskedLiveData.value ?: return

        when (pinLength) {
            5 -> {
                binding.fiveDigitPinView.apply {
                    pinFirstDigit.text = pinValue.getOrNull(0)?.toString() ?: ""
                    pinSecondDigit.text = pinValue.getOrNull(1)?.toString() ?: ""
                    pinThirdDigit.text = pinValue.getOrNull(2)?.toString() ?: ""
                    pinForthDigit.text = pinValue.getOrNull(3)?.toString() ?: ""
                    pinFifthDigit.text = pinValue.getOrNull(4)?.toString() ?: ""
                }
            }
            6 -> {
                binding.sixDigitPinView.apply {
                    pinFirstDigit.text = pinValue.getOrNull(0)?.toString() ?: ""
                    pinSecondDigit.text = pinValue.getOrNull(1)?.toString() ?: ""
                    pinThirdDigit.text = pinValue.getOrNull(2)?.toString() ?: ""
                    pinForthDigit.text = pinValue.getOrNull(3)?.toString() ?: ""
                    pinFifthDigit.text = pinValue.getOrNull(4)?.toString() ?: ""
                    pinSixthDigit.text = pinValue.getOrNull(5)?.toString() ?: ""
                }
            }
            else -> {
                binding.fourDigitPinView.apply {
                    pinFirstDigit.text = pinValue.getOrNull(0)?.toString() ?: ""
                    pinSecondDigit.text = pinValue.getOrNull(1)?.toString() ?: ""
                    pinThirdDigit.text = pinValue.getOrNull(2)?.toString() ?: ""
                    pinForthDigit.text = pinValue.getOrNull(3)?.toString() ?: ""
                }
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

    interface OnFragmentInteractionListener {
        fun onViewPinFragmentCompletion(response: SuccessErrorResponse)
    }
}
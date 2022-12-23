package ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NILabels
import ae.network.nicardmanagementsdk.api.models.input.UIFont
import ae.network.nicardmanagementsdk.databinding.FragmentCardDetailsBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class CardDetailsFragment : Fragment() {
    private lateinit var viewModel: CardDetailsFragmentViewModel
    private var _binding: FragmentCardDetailsBinding? = null
    private val binding: FragmentCardDetailsBinding
        get() = _binding!!
    private lateinit var niInput: NIInput
    private var listener: OnFragmentInteractionListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("${context::class.java.simpleName} must implement CardDetailsFragment.OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializableCompat(Extra.EXTRA_NI_INPUT, NIInput::class.java)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_INPUT} is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = Injector.getInstance(requireActivity()).provideCardDetailsFragmentViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[CardDetailsFragmentViewModel::class.java]
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_details, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initializeUI() {
        niInput.displayAttributes?.let { niDisplayAttributes ->
            niDisplayAttributes.fonts?.let { niFontLabelPairs ->
            niFontLabelPairs.forEach {
                when (it.label) {
                    NILabels.CARD_NUMBER_LABEL -> setCardFonts(binding.cardNumberLabelTextView, it.uiFont)
                    NILabels.CARD_NUMBER_VALUE_LABEL -> setCardFonts(binding.cardNumberTextView, it.uiFont)
                    NILabels.EXPIRY_DATE_LABEL -> setCardFonts(binding.expiryDateLabelTextView, it.uiFont)
                    NILabels.EXPIRY_DATE_VALUE_LABEL -> setCardFonts(binding.expiryDateTextView, it.uiFont)
                    NILabels.CVV_LABEL -> setCardFonts(binding.cvvCodeLabelTextView, it.uiFont)
                    NILabels.CVV_VALUE_LABEL -> setCardFonts(binding.cvvCodeTextView, it.uiFont)
                    NILabels.CARD_HOLDER_NAME_LABEL -> setCardFonts(binding.cardHolderNameTextView, it.uiFont)
                    else -> { }
                    }
                }
            }
            niDisplayAttributes.cardAttributes?.let { niCardAttributes ->
                viewModel.defaultShouldDisplayValue = !niCardAttributes.shouldHide
                niCardAttributes.backgroundImage?.let { it -> binding.cardMarginImageView.setImageResource(it) }
            }
        }

        niInput.displayAttributes?.let { niDisplayAttributes ->
            niDisplayAttributes.fonts?.let { niFontLabelPairs ->
            niFontLabelPairs.forEach {
                when (it.label) {
                    NILabels.CARD_NUMBER_LABEL -> setCardFonts(binding.cardNumberLabelTextView, it.uiFont)
                    NILabels.CARD_NUMBER_VALUE_LABEL -> setCardFonts(binding.cardNumberTextView, it.uiFont)
                    NILabels.EXPIRY_DATE_LABEL -> setCardFonts(binding.expiryDateLabelTextView, it.uiFont)
                    NILabels.EXPIRY_DATE_VALUE_LABEL -> setCardFonts(binding.expiryDateTextView, it.uiFont)
                    NILabels.CVV_LABEL -> setCardFonts(binding.cvvCodeLabelTextView, it.uiFont)
                    NILabels.CVV_VALUE_LABEL -> setCardFonts(binding.cvvCodeTextView, it.uiFont)
                    NILabels.CARD_HOLDER_NAME_LABEL -> setCardFonts(binding.cardHolderNameTextView, it.uiFont)
                    else -> { }
                    }
                }
            }
            niDisplayAttributes.cardAttributes?.let { niCardAttributes ->
                viewModel.defaultShouldDisplayValue = !niCardAttributes.shouldHide
                niCardAttributes.backgroundImage?.let { it -> binding.cardMarginImageView.setImageResource(it) }
            }
        }

        val clipboardManager =
            requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager

        binding.copyCardNumberImageView.setOnClickListener {
            val cardNumber = viewModel.getClearPanNonSpaced
            viewModel.copyToClipboard(cardNumber, clipboardManager, R.string.copied_to_clipboard)
        }

        binding.copyCardHolderNameImageView.setOnClickListener {
            val cardHolderName = viewModel.getClearCardHolderName
            viewModel.copyToClipboard(cardHolderName, clipboardManager, R.string.copied_to_clipboard)
        }

        binding.hideShowDetailsImageView.setOnClickListener {
            viewModel.isShowDetailsLiveData.value?.let { isVisible ->
                viewModel.isShowDetailsLiveData.value = !isVisible
            }
        }

        viewModel.copiedTextMessageSingleLiveEvent.observe(this) { resId ->
            resId?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.onResultSingleLiveEvent.observe(this) { successErrorResponse ->
            successErrorResponse?.let {
                listener?.onCardDetailsFragmentCompletion(it)
            }
        }

        if (viewModel.isShowDetailsLiveData.value == null) {
            viewModel.getData()
        }
    }

    private fun setCardFonts(textView: TextView, uiFont: UIFont?) {
        uiFont?.let {
            textView.apply {
                uiFont.fontRes?.let {
                    typeface = ResourcesCompat.getFont(context, it)
                }
                textSize = uiFont.textSize.toFloat()
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onCardDetailsFragmentCompletion(response: SuccessErrorResponse)
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput) = CardDetailsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
            }
        }

        const val TAG = "CardDetailsFragment"
    }
}

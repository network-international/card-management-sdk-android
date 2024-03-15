package ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementsPositioning
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NILabels
import ae.network.nicardmanagementsdk.api.models.input.UIFont
import ae.network.nicardmanagementsdk.databinding.FragmentCardDetailsBinding
import ae.network.nicardmanagementsdk.databinding.FragmentCardDetailsFreeformBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class CardDetailsFragmentFreeForm : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, positioning: CardElementsPositioning, elementsColor: Int) = CardDetailsFragmentFreeForm().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_NI_CARD_ELEMENTS_POSITIONING, positioning)
                putSerializable(Extra.EXTRA_NI_CARD_ELEMENTS_COLOR, elementsColor)
            }
        }

        const val TAG = "CardDetailsFragmentFreeForm"
    }

    private lateinit var viewModel: CardDetailsFragmentViewModel
    private var _binding: FragmentCardDetailsFreeformBinding? = null
    private val binding: FragmentCardDetailsFreeformBinding
        get() = _binding!!
    private lateinit var niInput: NIInput
    private lateinit var positioning: CardElementsPositioning
    private var elementsColor: Int? = null
    protected var listener: CardDetailsFragmentBase.OnFragmentInteractionListener? = null

    // override
    fun checkSubscriber(context: Context) {
        if (parentFragment is CardDetailsFragmentBase.OnFragmentInteractionListener) {
            listener = parentFragment as CardDetailsFragmentBase.OnFragmentInteractionListener
        } else if (context is CardDetailsFragmentBase.OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$parentFragment must implement OnFragmentInteractionListener")
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

        arguments?.getSerializableCompat<CardElementsPositioning>(Extra.EXTRA_NI_CARD_ELEMENTS_POSITIONING)?.let {
            positioning = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_CARD_ELEMENTS_POSITIONING} is missing")

        arguments?.getSerializableCompat<Int>(Extra.EXTRA_NI_CARD_ELEMENTS_COLOR)?.let {
            elementsColor = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = Injector.getInstance(requireActivity()).provideCardDetailsFragmentViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[CardDetailsFragmentViewModel::class.java]
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_details_freeform, container, false)
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
                    NILabels.CARD_HOLDER_NAME_LABEL -> setCardFonts(binding.cardHolderNameLabelTextView, it.uiFont)
                    NILabels.CARD_HOLDER_NAME_VALUE_LABEL -> setCardFonts(binding.cardHolderNameTextView, it.uiFont)
                    else -> { }
                    }
                }
            }
            niDisplayAttributes.cardAttributes?.let { niCardAttributes ->
                viewModel.defaultShouldDisplayValue = !niCardAttributes.shouldHide
            }
        }

        elementsColor?.let { color ->
            // ContextCompat.getColor(context, R.color.some_color)
            binding.cardNumberLabelTextView.setTextColor(color)
            binding.cardNumberTextView.setTextColor(color)
            binding.expiryDateLabelTextView.setTextColor(color)
            binding.expiryDateTextView.setTextColor(color)
            binding.cvvCodeLabelTextView.setTextColor(color)
            binding.cvvCodeTextView.setTextColor(color)
            binding.cardHolderNameLabelTextView.setTextColor(color)
            binding.cardHolderNameTextView.setTextColor(color)

            binding.copyCardNumberImageView.setTint(color)
            binding.hideShowDetailsImageView.setTint(color)
            binding.copyCardHolderNameImageView.setTint(color)
        }

        positioning.cardNumberLabel?.let { positioning ->
            positioning.start?.let { it -> binding.cardNumberLabelStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cardNumberLabelTop.setGuidelineBegin(it) }
        }
        positioning.cardNumberText?.let { positioning ->
            positioning.start?.let { it -> binding.cardNumberTextStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cardNumberTextTop.setGuidelineBegin(it) }
        }
        positioning.cardNumberButton?.let { positioning ->
            positioning.start?.let { it -> binding.cardNumberButtonStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cardNumberButtonTop.setGuidelineBegin(it) }
        }
        positioning.expiryLabel?.let { positioning ->
            positioning.start?.let { it -> binding.expiryLabelStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.expiryLabelTop.setGuidelineBegin(it) }
        }
        positioning.expiryText?.let { positioning ->
            positioning.start?.let { it -> binding.expiryTextStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.expiryTextTop.setGuidelineBegin(it) }
        }
        positioning.showDetailsButton?.let { positioning ->
            positioning.start?.let { it -> binding.showDetailsButtonStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.showDetailsButtonTop.setGuidelineBegin(it) }
        }
        positioning.cvvLabel?.let { positioning ->
            positioning.start?.let { it -> binding.cvvLabelStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cvvLabelTop.setGuidelineBegin(it) }
        }
        positioning.cvvText?.let { positioning ->
            positioning.start?.let { it -> binding.cvvTextStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cvvTextTop.setGuidelineBegin(it) }
        }
        positioning.cardHolderLabel?.let { positioning ->
            positioning.start?.let { it -> binding.cardHolderLabelStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cardHolderLabelTop.setGuidelineBegin(it) }
        }
        positioning.cardHolderText?.let { positioning ->
            positioning.start?.let { it -> binding.cardHolderTextStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cardHolderTextTop.setGuidelineBegin(it) }
        }
        positioning.cardHolderButton?.let { positioning ->
            positioning.start?.let { it -> binding.cardHolderButtonStart.setGuidelineBegin(it) }
            positioning.top?.let { it -> binding.cardHolderButtonTop.setGuidelineBegin(it) }
        }


        val clipboardManager =
            requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager

        binding.copyCardNumberImageView.setOnClickListener {
            val cardNumber = viewModel.getClearPanNonSpaced
            viewModel.copyToClipboard(cardNumber, clipboardManager, getClipboardText())
        }

        binding.copyCardHolderNameImageView.setOnClickListener {
            val cardHolderName = viewModel.getClearCardHolderName
            viewModel.copyToClipboard(cardHolderName, clipboardManager, getClipboardText())
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

        binding.shouldDefaultLanguage = when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> false
            else -> true
        }
    }

    private fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }

    private fun ImageView.setTint3(@ColorInt color: Int?) {
        if (color == null) {
            ImageViewCompat.setImageTintList(this, null)
            return
        }
        ImageViewCompat.setImageTintMode(this, PorterDuff.Mode.SRC_ATOP)
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
    }
    private fun ImageView.setTint2(context: Context, @ColorRes colorId: Int) {
        val color = ContextCompat.getColor(context, colorId)
        val colorStateList = ColorStateList.valueOf(color)
        ImageViewCompat.setImageTintList(this, colorStateList)
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

    private fun getClipboardText(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.copied_to_clipboard_ar
            else -> R.string.copied_to_clipboard_en
        }
}

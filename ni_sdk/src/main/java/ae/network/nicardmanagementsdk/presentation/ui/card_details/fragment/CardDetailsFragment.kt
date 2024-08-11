package ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementLayout
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.CardElementsItemConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NILabels
import ae.network.nicardmanagementsdk.api.models.input.UIFont
import ae.network.nicardmanagementsdk.databinding.FragmentCardDetailsFreeformBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.extension_methods.setColorRes
import ae.network.nicardmanagementsdk.presentation.extension_methods.setConstraints
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

interface CardDetailsFragmentListener {
    fun onCardDetailsFragmentCompletion(response: SuccessErrorResponse)
}
class CardDetailsFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, config: CardElementsConfig) = CardDetailsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_NI_CARD_ELEMENTS_CONFIG, config)
            }
        }

        const val TAG = "CardDetailsFragment"
    }

    private lateinit var viewModel: CardDetailsFragmentViewModel
    private var _binding: FragmentCardDetailsFreeformBinding? = null
    private val binding: FragmentCardDetailsFreeformBinding
        get() = _binding!!
    private lateinit var niInput: NIInput
    private lateinit var elementsConfig: CardElementsConfig
    private var listener: CardDetailsFragmentListener? = null

    // override
    fun checkSubscriber(context: Context) {
        listener = if (parentFragment is CardDetailsFragmentListener) {
            parentFragment as CardDetailsFragmentListener
        } else if (context is CardDetailsFragmentListener) {
            context
        } else {
            throw RuntimeException("Must implement CardDetailsFragmentListener")
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

        arguments?.getSerializableCompat<CardElementsConfig>(Extra.EXTRA_NI_CARD_ELEMENTS_CONFIG)?.let {
            elementsConfig = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_CARD_ELEMENTS_CONFIG} is missing")
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
        super.onViewCreated(view, savedInstanceState)
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
                    }
                }
            }
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

        // Additional buttons
        binding.copyCVVImageView.setOnClickListener {
            val clearValue = viewModel.getClearCVV
            viewModel.copyToClipboard(clearValue, clipboardManager, getClipboardText())
        }
        binding.copyExpiryImageView.setOnClickListener {
            val clearValue = viewModel.getClearExpiry
            viewModel.copyToClipboard(clearValue, clipboardManager, getClipboardText())
        }

        binding.hideShowDetailsImageView.setOnClickListener {
            viewModel.showMaskedLiveData.value?.let { currentMasked ->
                var anyTargetCurrentlyMasked = false
                for (target in elementsConfig.commonMaskButtonTargets) {
                    if (currentMasked.contains(target)) {
                        anyTargetCurrentlyMasked = true
                        break
                    }
                }
                val filtered = currentMasked.filter { !elementsConfig.commonMaskButtonTargets.contains(it) }
                if (anyTargetCurrentlyMasked) { // unmask all targets
                    viewModel.showMaskedLiveData.value = filtered
                } else { // mask all targets
                    viewModel.showMaskedLiveData.value = (elementsConfig.commonMaskButtonTargets + filtered)
                }
            }
        }

        // Additional buttons
        binding.hideShowCardHolderDetailsImageView.setOnClickListener {
            viewModel.showMaskedLiveData.value?.let { currentMasked ->
                if (currentMasked.contains(CardMaskableElement.CARDHOLDER)) {
                    viewModel.showMaskedLiveData.value = currentMasked.filter { it != CardMaskableElement.CARDHOLDER }
                } else {
                    val included = currentMasked + CardMaskableElement.CARDHOLDER
                    viewModel.showMaskedLiveData.value = included
                }
            }
        }
        binding.hideShowCardNumberDetailsImageView.setOnClickListener {
            viewModel.showMaskedLiveData.value?.let { currentMasked ->
                if (currentMasked.contains(CardMaskableElement.CARDNUMBER)) {
                    viewModel.showMaskedLiveData.value = currentMasked.filter { it != CardMaskableElement.CARDNUMBER }
                } else {
                    val included = currentMasked + CardMaskableElement.CARDNUMBER
                    viewModel.showMaskedLiveData.value = included
                }
            }
        }
        binding.hideShowExpiryImageView.setOnClickListener {
            viewModel.showMaskedLiveData.value?.let { currentMasked ->
                if (currentMasked.contains(CardMaskableElement.EXPIRY)) {
                    viewModel.showMaskedLiveData.value = currentMasked.filter { it != CardMaskableElement.EXPIRY }
                } else {
                    val included = currentMasked + CardMaskableElement.EXPIRY
                    viewModel.showMaskedLiveData.value = included
                }
            }
        }
        binding.hideShowCVVImageView.setOnClickListener {
            viewModel.showMaskedLiveData.value?.let { currentMasked ->
                if (currentMasked.contains(CardMaskableElement.CVV)) {
                    viewModel.showMaskedLiveData.value = currentMasked.filter { it != CardMaskableElement.CVV }
                } else {
                    val included = currentMasked + CardMaskableElement.CVV
                    viewModel.showMaskedLiveData.value = included
                }
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

        if (viewModel.showMaskedLiveData.value == null) {
            viewModel.getData()
        }

        val shouldDefaultLanguage = when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> false
            else -> true
        }


        // set default labels - it can be overriden by config
        if (shouldDefaultLanguage) {
            binding.cardNumberLabelTextView.setText(R.string.card_number_en)
            binding.expiryDateLabelTextView.setText(R.string.card_expiry_en)
            binding.cvvCodeLabelTextView.setText(R.string.card_cvv_en)
            binding.cardHolderNameLabelTextView.setText(R.string.card_name_en)
        } else {
            binding.cardNumberLabelTextView.setText(R.string.card_number_ar)
            binding.expiryDateLabelTextView.setText(R.string.card_expiry_ar)
            binding.cvvCodeLabelTextView.setText(R.string.card_cvv_ar)
            binding.cardHolderNameLabelTextView.setText(R.string.card_name_ar)
        }
        elementsConfig.let { cnf ->
            cnf.cardHolder?.let { elm ->
                // resources
                elm.labelResource?.let { it -> binding.cardHolderNameLabelTextView.setText(it) }
                elm.labelColor?.let { colorRes -> binding.cardHolderNameLabelTextView.setColorRes(colorRes) }
                elm.detailsColor?.let { colorRes -> binding.cardHolderNameTextView.setColorRes(colorRes) }
                elm.copyButtonImage?.let { it -> binding.copyCardHolderNameImageView.setImageResource(it) } // set drawable
                // resources will be assigned in `setButtonsVisibility`
                //elm.maskButtonShowImage?.let { it ->  }
                //elm.maskButtonHideImage?.let { it ->  }

                // layout
                elm.labelLayout?.let { it -> binding.cardHolderNameLabelTextView.setConstraints(it, binding.constraintLayout) }
                elm.detailsLayout?.let { it -> binding.cardHolderNameTextView.setConstraints(it, binding.constraintLayout) }
                elm.copyButtonLayout?.let { it -> binding.copyCardHolderNameImageViewHolder.setConstraints(it, binding.constraintLayout) }
                elm.maskButtonLayout?.let { it -> binding.hideShowCardHolderDetailsImageViewHolder.setConstraints(it, binding.constraintLayout) }
            }
            cnf.cardNumber?.let { elm ->
                // resources
                elm.labelResource?.let { it -> binding.cardNumberLabelTextView.setText(it) }
                elm.labelColor?.let { colorRes -> binding.cardNumberLabelTextView.setColorRes(colorRes) }
                elm.detailsColor?.let { colorRes -> binding.cardNumberTextView.setColorRes(colorRes) }
                elm.copyButtonImage?.let { it -> binding.copyCardNumberImageView.setImageResource(it) } // set drawable
                // resources will be assigned in `setButtonsVisibility`
                //elm.maskButtonShowImage?.let { it ->  }
                //elm.maskButtonHideImage?.let { it ->  }

                // layout
                elm.labelLayout?.let { it -> binding.cardNumberLabelTextView.setConstraints(it, binding.constraintLayout) }
                elm.detailsLayout?.let { it -> binding.cardNumberTextView.setConstraints(it, binding.constraintLayout) }
                elm.copyButtonLayout?.let { it -> binding.copyCardNumberImageViewHolder.setConstraints(it, binding.constraintLayout) }
                elm.maskButtonLayout?.let { it -> binding.hideShowCardNumberDetailsImageViewHolder.setConstraints(it, binding.constraintLayout) }
            }
            cnf.cvv?.let { elm ->
                // resources
                elm.labelResource?.let { it -> binding.cvvCodeLabelTextView.setText(it) }
                elm.labelColor?.let { colorRes -> binding.cvvCodeLabelTextView.setColorRes(colorRes) }
                elm.detailsColor?.let { colorRes -> binding.cvvCodeTextView.setColorRes(colorRes) }
                elm.copyButtonImage?.let { it -> binding.copyCVVImageView.setImageResource(it) }
                // resources will be assigned in `setButtonsVisibility`
                //elm.maskButtonShowImage?.let { it -> /*TODO: add button*/ }
                //elm.maskButtonHideImage?.let { it -> /*TODO: add button*/ }

                // layout
                elm.labelLayout?.let { it -> binding.cvvCodeLabelTextView.setConstraints(it, binding.constraintLayout) }
                elm.detailsLayout?.let { it -> binding.cvvCodeTextView.setConstraints(it, binding.constraintLayout) }
                elm.copyButtonLayout?.let { it -> binding.copyCVVImageViewHolder.setConstraints(it, binding.constraintLayout) }
                elm.maskButtonLayout?.let { it -> binding.hideShowCVVImageViewHolder.setConstraints(it, binding.constraintLayout) }
            }

            cnf.expiry?.let { elm ->
                // resources
                elm.labelResource?.let { it -> binding.expiryDateLabelTextView.setText(it) }
                elm.labelColor?.let { colorRes -> binding.expiryDateLabelTextView.setColorRes(colorRes) }
                elm.detailsColor?.let { colorRes -> binding.expiryDateTextView.setColorRes(colorRes) }
                elm.copyButtonImage?.let { it -> binding.copyExpiryImageView.setImageResource(it) }
                // resources will be assigned in `setButtonsVisibility`
                //elm.maskButtonShowImage?.let { it -> /*TODO: add button*/ }
                //elm.maskButtonHideImage?.let { it -> /*TODO: add button*/ }

                // layout
                elm.labelLayout?.let { it -> binding.expiryDateLabelTextView.setConstraints(it, binding.constraintLayout) }
                elm.detailsLayout?.let { it -> binding.expiryDateTextView.setConstraints(it, binding.constraintLayout) }
                elm.copyButtonLayout?.let { it -> binding.copyExpiryImageViewHolder.setConstraints(it, binding.constraintLayout) }
                elm.maskButtonLayout?.let { it -> binding.hideShowExpiryImageViewHolder.setConstraints(it, binding.constraintLayout) }
            }
            cnf.commonMaskButton?.let { elm ->
                // layout
                elm.maskButtonLayout?.let { it -> binding.hideShowDetailsImageViewHolder.setConstraints(it, binding.constraintLayout) }
                // resources will be assigned in `setButtonsVisibility`
                //elm.maskButtonShowImage?.let { it -> /*TODO: add button*/ }
                //elm.maskButtonHideImage?.let { it -> /*TODO: add button*/ }
            }
            cnf.shouldBeMaskedDefault.let { targets ->
                viewModel.shouldBeMaskedDefault = targets
            }
            cnf.progressBar?.let { elm ->
                elm.detailsColor?.let { colorRes -> //binding.loadingIndicator.setColorRes(colorRes)
                    val tint = ContextCompat.getColor(binding.loadingIndicator.context, colorRes)
                    binding.loadingIndicator.indeterminateDrawable.setTint(tint)
                    //setColorFilter(0xFFFF0000.toInt(), android.graphics.PorterDuff.Mode.MULTIPLY)
                }
                elm.detailsLayout?.let {
                    it.left?.let { itt -> binding.loadingIndicator.updatePadding(left = itt) }
                    it.right?.let { itt -> binding.loadingIndicator.updatePadding(right = itt) }
                    it.top?.let { itt -> binding.loadingIndicator.updatePadding(top = itt) }
                    it.bottom?.let { itt -> binding.loadingIndicator.updatePadding(bottom = itt) }
                }
            }
        }


        setButtonsVisibility(null) // hide all
        viewModel.showMaskedLiveData.observe(viewLifecycleOwner) { showMaskedLiveData ->
            setButtonsVisibility(null) // hide all
            setButtonsVisibility(showMaskedLiveData)
        }
    }

    private fun setButtonsVisibility(showMaskedLiveData: List<CardMaskableElement>?) {
        // Additional buttons
        binding.hideShowCardHolderDetailsImageViewHolder.visibility = View.INVISIBLE
        binding.hideShowCardNumberDetailsImageViewHolder.visibility = View.INVISIBLE
        binding.copyCVVImageViewHolder.visibility = View.INVISIBLE
        binding.hideShowCVVImageViewHolder.visibility = View.INVISIBLE
        binding.copyExpiryImageViewHolder.visibility = View.INVISIBLE
        binding.hideShowExpiryImageViewHolder.visibility = View.INVISIBLE

        binding.copyCardNumberImageViewHolder.visibility = View.INVISIBLE
        binding.copyCardHolderNameImageViewHolder.visibility = View.INVISIBLE
        binding.hideShowDetailsImageViewHolder.visibility = View.INVISIBLE

        showMaskedLiveData?.let {
            // common mask button
            if (elementsConfig.commonMaskButton?.maskButtonShowImage != null || elementsConfig.commonMaskButton?.maskButtonHideImage != null) {
                var anyTargetCurrentlyMasked = false
                for (target in elementsConfig.commonMaskButtonTargets) {
                    if (it.contains(target)) {
                        anyTargetCurrentlyMasked = true
                        break
                    }
                }
                if (!anyTargetCurrentlyMasked) { // try set `show` image first then correct it with `hide` image
                    elementsConfig.commonMaskButton?.maskButtonShowImage?.let { binding.hideShowDetailsImageView.setImageResource(it) }
                    elementsConfig.commonMaskButton?.maskButtonHideImage?.let { binding.hideShowDetailsImageView.setImageResource(it) }
                } else { // if any target field masked --> allow unmast all targets
                    elementsConfig.commonMaskButton?.maskButtonHideImage?.let { binding.hideShowDetailsImageView.setImageResource(it) }
                    elementsConfig.commonMaskButton?.maskButtonShowImage?.let { binding.hideShowDetailsImageView.setImageResource(it) }
                }
                binding.hideShowDetailsImageViewHolder.visibility = View.VISIBLE
            }
            // CardHolder
            if (elementsConfig.cardHolder?.maskButtonShowImage != null || elementsConfig.cardHolder?.maskButtonHideImage != null) {
                if (it.contains(CardMaskableElement.CARDHOLDER)) {
                    elementsConfig.cardHolder?.maskButtonHideImage?.let { binding.hideShowCardHolderDetailsImageView.setImageResource(it) }
                    elementsConfig.cardHolder?.maskButtonShowImage?.let { binding.hideShowCardHolderDetailsImageView.setImageResource(it) }
                } else {
                    elementsConfig.cardHolder?.maskButtonShowImage?.let { binding.hideShowCardHolderDetailsImageView.setImageResource(it) }
                    elementsConfig.cardHolder?.maskButtonHideImage?.let { binding.hideShowCardHolderDetailsImageView.setImageResource(it) }
                }
                binding.hideShowCardHolderDetailsImageViewHolder.visibility = View.VISIBLE
            }
            if (!(it.contains(CardMaskableElement.CARDHOLDER))) {
                elementsConfig.cardHolder?.copyButtonImage?.let {
                    binding.copyCardHolderNameImageView.setImageResource(it)
                    binding.copyCardHolderNameImageViewHolder.visibility = View.VISIBLE
                }
            }
            // CardNumber
            if (elementsConfig.cardNumber?.maskButtonShowImage != null || elementsConfig.cardNumber?.maskButtonHideImage != null) {
                if (it.contains(CardMaskableElement.CARDNUMBER)) {
                    elementsConfig.cardNumber?.maskButtonHideImage?.let { binding.hideShowCardNumberDetailsImageView.setImageResource(it) }
                    elementsConfig.cardNumber?.maskButtonShowImage?.let { binding.hideShowCardNumberDetailsImageView.setImageResource(it) }
                } else {
                    elementsConfig.cardNumber?.maskButtonShowImage?.let { binding.hideShowCardNumberDetailsImageView.setImageResource(it) }
                    elementsConfig.cardNumber?.maskButtonHideImage?.let { binding.hideShowCardNumberDetailsImageView.setImageResource(it) }
                }
                binding.hideShowCardNumberDetailsImageViewHolder.visibility = View.VISIBLE
            }
            if (!(it.contains(CardMaskableElement.CARDNUMBER))) {
                elementsConfig.cardNumber?.copyButtonImage?.let {
                    binding.copyCardNumberImageView.setImageResource(it)
                    binding.copyCardNumberImageViewHolder.visibility = View.VISIBLE
                }
            }
            // CVV
            if (elementsConfig.cvv?.maskButtonShowImage != null || elementsConfig.cvv?.maskButtonHideImage != null) {
                if (it.contains(CardMaskableElement.CVV)) {
                    elementsConfig.cvv?.maskButtonHideImage?.let { binding.hideShowCVVImageView.setImageResource(it) }
                    elementsConfig.cvv?.maskButtonShowImage?.let { binding.hideShowCVVImageView.setImageResource(it) }
                } else {
                    elementsConfig.cvv?.maskButtonShowImage?.let { binding.hideShowCVVImageView.setImageResource(it) }
                    elementsConfig.cvv?.maskButtonHideImage?.let { binding.hideShowCVVImageView.setImageResource(it) }
                }
                binding.hideShowCVVImageViewHolder.visibility = View.VISIBLE
            }
            if (!(it.contains(CardMaskableElement.CVV))) {
                elementsConfig.cvv?.copyButtonImage?.let {
                    binding.copyCVVImageViewHolder.visibility = View.VISIBLE
                }
            }
            // Expiry
            if (elementsConfig.expiry?.maskButtonShowImage != null || elementsConfig.expiry?.maskButtonHideImage != null) {
                if (it.contains(CardMaskableElement.EXPIRY)) {
                    elementsConfig.expiry?.maskButtonHideImage?.let { binding.hideShowExpiryImageView.setImageResource(it) }
                    elementsConfig.expiry?.maskButtonShowImage?.let { binding.hideShowExpiryImageView.setImageResource(it) }
                } else {
                    elementsConfig.expiry?.maskButtonShowImage?.let { binding.hideShowExpiryImageView.setImageResource(it) }
                    elementsConfig.expiry?.maskButtonHideImage?.let { binding.hideShowExpiryImageView.setImageResource(it) }
                }
                binding.hideShowExpiryImageViewHolder.visibility = View.VISIBLE
            }
            if (!(it.contains(CardMaskableElement.EXPIRY))) {
                elementsConfig.expiry?.copyButtonImage?.let {
                    binding.copyExpiryImageView.setImageResource(it)
                    binding.copyExpiryImageViewHolder.visibility = View.VISIBLE
                }
            }
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

    private fun getClipboardText(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.copied_to_clipboard_ar
            else -> R.string.copied_to_clipboard_en
        }
}


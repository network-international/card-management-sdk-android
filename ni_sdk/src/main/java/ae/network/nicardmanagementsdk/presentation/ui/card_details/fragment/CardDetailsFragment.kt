package ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.databinding.FragmentCardDetailsBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.extension_methods.setCardElementText
import ae.network.nicardmanagementsdk.presentation.extension_methods.setConstraints
import ae.network.nicardmanagementsdk.presentation.extension_methods.setContentDescrText
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        fun newInstance(
            input: NIInput,
            copyToClipboardMessage: Int, // Only show a toast for Android 12 and lower.
            config: CardElementsConfig
        ) = CardDetailsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_NI_CARD_ELEMENTS_CONFIG, config)
                putSerializable(Extra.EXTRA_NI_COPY_CLIPBOARD_TEXT, copyToClipboardMessage)
            }
        }

        const val TAG = "CardDetailsFragment"
    }

    private lateinit var viewModel: CardDetailsFragmentViewModel
    private var _binding: FragmentCardDetailsBinding? = null
    private val binding: FragmentCardDetailsBinding
        get() = _binding!!
    private lateinit var niInput: NIInput
    private lateinit var elementsConfig: CardElementsConfig
    @StringRes private var copyToClipboardMessage: Int = R.string.copied_to_clipboard_en
    private var listener: CardDetailsFragmentListener? = null

    private var isFetchRequested = false
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

        arguments?.getSerializableCompat<Int>(Extra.EXTRA_NI_COPY_CLIPBOARD_TEXT)?.let {
            copyToClipboardMessage = it
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = Injector.getInstance(requireActivity()).provideCardDetailsFragmentViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[CardDetailsFragmentViewModel::class.java]
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_details, container, false)
        binding.lifecycleOwner = this
        //binding.viewModel = viewModel
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
        binding.copyCardNumberImageView.setOnClickListener {
            elementsConfig.cardNumber?.copyButton?.let {
                copyToClipboard(it.targets, it.template)
            }
        }
        binding.copyCardHolderNameImageView.setOnClickListener {
            elementsConfig.cardHolder?.copyButton?.let {
                copyToClipboard(it.targets, it.template)
            }
        }
        binding.copyCVVImageView.setOnClickListener {
            elementsConfig.cvv?.copyButton?.let {
                copyToClipboard(it.targets, it.template)
            }
        }
        binding.copyExpiryImageView.setOnClickListener {
            elementsConfig.expiry?.copyButton?.let {
                copyToClipboard(it.targets, it.template)
            }
        }

        binding.hideShowDetailsImageView.setOnClickListener {
            elementsConfig.commonMaskButton?.targets?.let {
                viewModel.hideShowDetails(it)
            }
        }
        binding.hideShowCardHolderDetailsImageView.setOnClickListener {
            viewModel.hideShowDetails(listOf(CardMaskableElement.CARDHOLDER))
        }
        binding.hideShowCardNumberDetailsImageView.setOnClickListener {
            viewModel.hideShowDetails(listOf(CardMaskableElement.CARDNUMBER))
        }
        binding.hideShowExpiryImageView.setOnClickListener {
            viewModel.hideShowDetails(listOf(CardMaskableElement.EXPIRY))
        }
        binding.hideShowCVVImageView.setOnClickListener {
            viewModel.hideShowDetails(listOf(CardMaskableElement.CVV))
        }

        viewModel.fetchDataInitially()

        elementsConfig.let { cnf ->
            cnf.cardHolder?.let { elm ->
                elm.label?.let {
                    binding.cardHolderNameLabelTextView.setCardElementText(it.text)
                    it.appearanceResId?.let { binding.cardHolderNameLabelTextView.setTextAppearance(it) }
                    binding.cardHolderNameLabelTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.details.let {
                    it.appearanceResId?.let { binding.cardHolderNameTextView.setTextAppearance(it) }
                    binding.cardHolderNameTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.copyButton?.let {
                    binding.copyCardHolderNameImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.copyCardHolderNameImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.copyCardHolderNameImageView.setContentDescrText(it) }
                }
                elm.maskButton?.let {
                    binding.hideShowCardHolderDetailsImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.hideShowCardHolderDetailsImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.hideShowCardHolderDetailsImageView.setContentDescrText(it) }
                }
            }
            cnf.cardNumber?.let { elm ->
                elm.label?.let {
                    binding.cardNumberLabelTextView.setCardElementText(it.text)
                    it.appearanceResId?.let { binding.cardNumberLabelTextView.setTextAppearance(it) }
                    binding.cardNumberLabelTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.details.let {
                    it.appearanceResId?.let { binding.cardNumberTextView.setTextAppearance(it) }
                    binding.cardNumberTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.copyButton?.let {
                    binding.copyCardNumberImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.copyCardNumberImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.copyCardNumberImageView.setContentDescrText(it) }
                }
                elm.maskButton?.let {
                    binding.hideShowCardNumberDetailsImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.hideShowCardNumberDetailsImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.hideShowCardNumberDetailsImageView.setContentDescrText(it) }
                }
            }
            cnf.cvv?.let { elm ->
                elm.label?.let {
                    binding.cvvCodeLabelTextView.setCardElementText(it.text)
                    it.appearanceResId?.let { binding.cvvCodeLabelTextView.setTextAppearance(it) }
                    binding.cvvCodeLabelTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.details.let {
                    it.appearanceResId?.let { binding.cvvCodeTextView.setTextAppearance(it) }
                    binding.cvvCodeTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.copyButton?.let {
                    binding.copyCVVImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.copyCVVImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.copyCVVImageView.setContentDescrText(it) }
                }
                elm.maskButton?.let {
                    binding.hideShowCVVImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.hideShowCVVImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.hideShowCVVImageView.setContentDescrText(it) }
                }
            }
            cnf.expiry?.let { elm ->
                elm.label?.let {
                    binding.expiryDateLabelTextView.setCardElementText(it.text)
                    it.appearanceResId?.let { binding.expiryDateLabelTextView.setTextAppearance(it) }
                    binding.expiryDateLabelTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.details.let {
                    it.appearanceResId?.let { binding.expiryDateTextView.setTextAppearance(it) }
                    binding.expiryDateTextView.setConstraints(it.layout, binding.constraintLayout)
                }
                elm.copyButton?.let {
                    binding.copyExpiryImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.copyExpiryImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.copyExpiryImageView.setContentDescrText(it) }
                }
                elm.maskButton?.let {
                    binding.hideShowExpiryImageViewHolder.setConstraints(it.layout, binding.constraintLayout)
                    // selected image could be assigned in setButtonsVisibility
                    binding.hideShowExpiryImageView.setImageResource(it.imageDefault)
                    it.contentDescription?.let { binding.hideShowExpiryImageView.setContentDescrText(it) }
                }
            }
            cnf.commonMaskButton?.let { elm ->
                binding.hideShowDetailsImageViewHolder.setConstraints(elm.layout, binding.constraintLayout)
                // selected image could be assigned in setButtonsVisibility
                binding.hideShowDetailsImageView.setImageResource(elm.imageDefault)
                elm.contentDescription?.let { binding.hideShowDetailsImageView.setContentDescrText(it) }
            }
            cnf.shouldBeMaskedDefault.let { targets ->
                viewModel.shouldBeMaskedDefault = targets
            }
            cnf.progressBar?.let { elm ->
                elm.color?.let { colorRes -> //binding.loadingIndicator.setColorRes(colorRes)
                    val tint = ContextCompat.getColor(binding.loadingIndicator.context, colorRes)
                    binding.loadingIndicator.indeterminateDrawable.setTint(tint)
                    //setColorFilter(0xFFFF0000.toInt(), android.graphics.PorterDuff.Mode.MULTIPLY)
                }
                elm.paddingFromCenter?.let {
                    it.left?.let { itt -> binding.loadingIndicator.updatePadding(left = itt) }
                    it.right?.let { itt -> binding.loadingIndicator.updatePadding(right = itt) }
                    it.top?.let { itt -> binding.loadingIndicator.updatePadding(top = itt) }
                    it.bottom?.let { itt -> binding.loadingIndicator.updatePadding(bottom = itt) }
                }
            }
        }


        setButtonsVisibility(null) // hide all
        viewModel.maskedElementsLiveData.observe(viewLifecycleOwner) { showMaskedLiveData ->
            setButtonsVisibility(null)
            setButtonsVisibility(showMaskedLiveData)
        }
        viewModel.cardDetailsLiveData.observe(viewLifecycleOwner) { model ->
            binding.cardNumberTextView.text = model.pan
            binding.expiryDateTextView.text = model.expiry
            binding.cvvCodeTextView.text = model.cVV2
            binding.cardHolderNameTextView.text = model.cardholderName
        }
        viewModel.copiedTextMessageSingleLiveEvent.observe(this) { resId ->
            resId?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.progressBarHolder.visibility = View.GONE
        elementsConfig.progressBar?.let {
            binding.progressBarHolder.visibility = View.VISIBLE
        }
        viewModel.onResultSingleLiveEvent.observe(this) { successErrorResponse ->
            binding.progressBarHolder.visibility = View.GONE
            successErrorResponse?.let {
                listener?.onCardDetailsFragmentCompletion(it)
            }
        }
    }

    private fun setButtonsVisibility(showMaskedLiveData: List<CardMaskableElement>?) {
        hideAllUIControls()

        showMaskedLiveData?.let { maskedLiveData ->
            showLabelsIfExists()
            showMaskButtonsIfExists()
            updateCommonMaskButtonImage(maskedLiveData)
            updateDedicatedMaskButtonsImages(maskedLiveData)
            showCopyButtons(maskedLiveData)
        }
    }

    private fun hideAllUIControls() {
        binding.cardNumberLabelTextView.visibility = View.INVISIBLE
        binding.expiryDateLabelTextView.visibility = View.INVISIBLE
        binding.cvvCodeLabelTextView.visibility = View.INVISIBLE
        binding.cardHolderNameLabelTextView.visibility = View.INVISIBLE

        binding.copyCardHolderNameImageViewHolder.visibility = View.INVISIBLE
        binding.copyCardNumberImageViewHolder.visibility = View.INVISIBLE
        binding.copyCVVImageViewHolder.visibility = View.INVISIBLE
        binding.copyExpiryImageViewHolder.visibility = View.INVISIBLE

        binding.hideShowCardHolderDetailsImageViewHolder.visibility = View.INVISIBLE
        binding.hideShowCardNumberDetailsImageViewHolder.visibility = View.INVISIBLE
        binding.hideShowCVVImageViewHolder.visibility = View.INVISIBLE
        binding.hideShowExpiryImageViewHolder.visibility = View.INVISIBLE

        binding.hideShowDetailsImageViewHolder.visibility = View.INVISIBLE
    }

    private fun updateCommonMaskButtonImage(maskedLiveData: List<CardMaskableElement>) {
        // common mask button: change Image if image provided
        if (elementsConfig.commonMaskButton?.imageSelected != null) {
            var anyTargetCurrentlyMasked = false
            val targets = elementsConfig.commonMaskButton!!.targets
            for (target in targets) {
                if (maskedLiveData.contains(target)) {
                    anyTargetCurrentlyMasked = true
                    break
                }
            }
            if (anyTargetCurrentlyMasked) {
                elementsConfig.commonMaskButton?.imageDefault?.let { binding.hideShowDetailsImageView.setImageResource(it) }
            } else {
                elementsConfig.commonMaskButton?.imageSelected?.let { binding.hideShowDetailsImageView.setImageResource(it) }
            }
        }
    }

    private fun showCopyButtons(maskedLiveData: List<CardMaskableElement>) {
        if (!(maskedLiveData.contains(CardMaskableElement.CARDHOLDER))) {
            elementsConfig.cardHolder?.copyButton?.let {
                binding.copyCardHolderNameImageViewHolder.visibility = View.VISIBLE
            }
        }
        if (!(maskedLiveData.contains(CardMaskableElement.CARDNUMBER))) {
            elementsConfig.cardNumber?.copyButton?.let {
                binding.copyCardNumberImageViewHolder.visibility = View.VISIBLE
            }
        }
        if (!(maskedLiveData.contains(CardMaskableElement.CVV))) {
            elementsConfig.cvv?.copyButton?.let {
                binding.copyCVVImageViewHolder.visibility = View.VISIBLE
            }
        }
        if (!(maskedLiveData.contains(CardMaskableElement.EXPIRY))) {
            elementsConfig.expiry?.copyButton?.let {
                binding.copyExpiryImageViewHolder.visibility = View.VISIBLE
            }
        }
    }

    private fun updateDedicatedMaskButtonsImages(maskedLiveData: List<CardMaskableElement>) {
        if (!maskedLiveData.contains(CardMaskableElement.CARDHOLDER)) {
            elementsConfig.cardHolder?.maskButton?.imageSelected?.let { binding.hideShowCardHolderDetailsImageView.setImageResource(it) }
        } else {
            elementsConfig.cardHolder?.maskButton?.imageDefault?.let { binding.hideShowCardHolderDetailsImageView.setImageResource(it) }
        }

        if (!maskedLiveData.contains(CardMaskableElement.CARDNUMBER)) {
            elementsConfig.cardNumber?.maskButton?.imageSelected?.let { binding.hideShowCardNumberDetailsImageView.setImageResource(it) }
        } else {
            elementsConfig.cardNumber?.maskButton?.imageDefault?.let { binding.hideShowCardNumberDetailsImageView.setImageResource(it) }
        }

        if (!maskedLiveData.contains(CardMaskableElement.CVV)) {
            elementsConfig.cvv?.maskButton?.imageSelected?.let { binding.hideShowCVVImageView.setImageResource(it) }
        } else {
            elementsConfig.cvv?.maskButton?.imageDefault?.let { binding.hideShowCVVImageView.setImageResource(it) }
        }

        if (!maskedLiveData.contains(CardMaskableElement.EXPIRY)) {
            elementsConfig.expiry?.maskButton?.imageSelected?.let { binding.hideShowExpiryImageView.setImageResource(it) }
        } else {
            elementsConfig.expiry?.maskButton?.imageDefault?.let { binding.hideShowExpiryImageView.setImageResource(it) }
        }
    }

    private fun showMaskButtonsIfExists() {
        elementsConfig.commonMaskButton?.let {
            binding.hideShowDetailsImageViewHolder.visibility = View.VISIBLE
        }

        elementsConfig.cardHolder?.maskButton?.let {
            binding.hideShowCardHolderDetailsImageViewHolder.visibility = View.VISIBLE
        }
        elementsConfig.cardNumber?.maskButton?.let {
            binding.hideShowCardNumberDetailsImageViewHolder.visibility = View.VISIBLE
        }
        elementsConfig.cvv?.maskButton?.let {
            binding.hideShowCVVImageViewHolder.visibility = View.VISIBLE
        }
        elementsConfig.expiry?.maskButton?.let {
            binding.hideShowExpiryImageViewHolder.visibility = View.VISIBLE
        }
    }
    private fun showLabelsIfExists() {
        elementsConfig.cardHolder?.label?.let {
            binding.cardHolderNameLabelTextView.visibility = View.VISIBLE
        }
        elementsConfig.cardNumber?.label?.let {
            binding.cardNumberLabelTextView.visibility = View.VISIBLE
        }
        elementsConfig.cvv?.label?.let {
            binding.cvvCodeLabelTextView.visibility = View.VISIBLE
        }
        elementsConfig.expiry?.label?.let {
            binding.expiryDateLabelTextView.visibility = View.VISIBLE
        }
    }

    private fun copyToClipboard(targets: List<CardMaskableElement>, template: String?) {
        val clipboardManager =
            requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        viewModel.copyToClipboard(targets, template, clipboardManager, copyToClipboardMessage)
    }
}


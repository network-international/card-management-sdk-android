package ae.network.nicardmanagementsdk.sample

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.implementation.NICardManagement
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.CardPresenterConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.CardElementsPresenter
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragment
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentListener
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentViewModel
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import android.content.ClipboardManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nicardmanagementapp.databinding.CardDetailsDialogBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardBottomSheetDialogFragment : BottomSheetDialogFragment(), CardDetailsFragmentListener {

    private var _binding: CardDetailsDialogBinding? = null
    lateinit var niInput: NIInput

    private var isCvvMasked = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var presenter: CardElementsPresenter
    private // keep masked CVV only
    val maskableTargets = listOf<CardMaskableElement>(CardMaskableElement.CVV)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializableCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_INPUT} is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            CardDetailsDialogBinding.inflate(inflater, container, false)

        // The first method where it is safe to access the view lifecycle
        // Consider using getViewLifecycleOwnerLiveData() or FragmentTransaction. runOnCommit(Runnable)
        // to receive a callback for when the Fragment's view lifecycle is available.
//        viewLifecycleOwnerLiveData.observe(this) { owner ->
//            print("[viewLifecycleOwnerLiveData]")
//            print(owner)
//            Log.d("[viewLifecycleOwnerLiveData]", owner.toString())
//        }

        //binding.lifecycleOwner = this
        val presenterConfig = CardPresenterConfig.default()
        presenterConfig.shouldBeMaskedDefault = maskableTargets
        //val lifecycleOwner = requireContext().applicationContext
        //viewLifecycleOwner
        presenter = CardElementsPresenter.newInstance(requireContext(), this, niInput, presenterConfig)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cardHolderValueHolder.addView(presenter.cardHolder.data)
        binding.cardNumberValueHolder.addView(presenter.cardNumber.data)
        binding.cardCvvValueHolder.addView(presenter.cardCvv.data)
        binding.cardExpiryValueHolder.addView(presenter.cardExpiry.data)

        presenter.copiedTextMessageSingleLiveEvent.observe(this) { resId ->
            resId?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        presenter.onResultSingleLiveEvent.observe(this) { successErrorResponse ->
            binding.customProgressBarHolder.visibility = View.GONE
            successErrorResponse?.let {
                print("[Presenter] card request completed")
                print(it)
            }
        }

        presenter.fetch()

        binding.btnClose.setOnClickListener {
            dismiss()
        }
        // define fields for copy to clipboard
        binding.btnCopy.setOnClickListener {
            val copyTargets = listOf<CardMaskableElement>(
                CardMaskableElement.CARDNUMBER,
                CardMaskableElement.CARDHOLDER,
            )
            val copyTemplate = "Card number: %s\nName: %s"
            val clipboardManager =
                requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            presenter.copyToClipboard(copyTargets, copyTemplate, clipboardManager, R.string.copied_to_clipboard_en)
        }
        // define fields for masking
        binding.btnToggleCredentials.setOnClickListener {
            isCvvMasked = !isCvvMasked
            presenter.toggleDetails(isCvvMasked, maskableTargets)
        }
    }

    override fun onCardDetailsFragmentCompletion(response: SuccessErrorResponse) {
        response.isError?.let {
            lifecycleScope.launch {
                delay(500)
                dismiss()
            }
        }
    }


    companion object {
        fun newInstance(
            input: NIInput,
            config: CardElementsConfig
        ) = CardBottomSheetDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_NI_CARD_ELEMENTS_CONFIG, config)
            }
        }

        const val TAG = "CardBottomSheetDialogFragment"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

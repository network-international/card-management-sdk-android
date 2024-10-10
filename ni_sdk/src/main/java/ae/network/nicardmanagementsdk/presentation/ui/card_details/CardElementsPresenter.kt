package ae.network.nicardmanagementsdk.presentation.ui.card_details

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.interfaces.asSuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.api.models.input.CardPresenterConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.asClearPanNonSpaced
import ae.network.nicardmanagementsdk.api.models.output.asClearViewModel
import ae.network.nicardmanagementsdk.api.models.output.asMaskedViewModel
import ae.network.nicardmanagementsdk.core.GetCardDetailsCoreComponent
import ae.network.nicardmanagementsdk.core.IGetCardDetailsCore
import ae.network.nicardmanagementsdk.presentation.components.SingleLiveEvent
import ae.network.nicardmanagementsdk.presentation.extension_methods.setUIElementText
import ae.network.nicardmanagementsdk.presentation.models.CardDetailsModel
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElementEntries
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CardElement(
    private val context: Context,
    @StyleRes private val titleAppearanceResId: Int?,
    @StyleRes private val dataAppearanceResId: Int?,
    private val titleText: UIElementText?,
) {
    val title: View by lazy {
        makeTextView(titleAppearanceResId, context, titleText)
    }

    val data: View by lazy {
        makeTextView(dataAppearanceResId, context, null)
    }

    fun updateData(txt: String?) {
        (data as TextView).text = txt
    }
    private fun makeLayoutParams(): ViewGroup.LayoutParams {
        // probably views should live in FrameLayout
        return FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun makeTextView(@StyleRes textAppearance: Int?, context: Context, text: UIElementText?): TextView {
        val textView = TextView(context)
        textView.id = ViewCompat.generateViewId() // MUST have valid id for constraints
        textView.layoutParams = makeLayoutParams()
        textAppearance?.let { textView.setTextAppearance(it) }
        text?.let { textView.setUIElementText(it) }
        return textView
    }
}
class CardElementsPresenter(
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
    val niInput: NIInput,
    val config: CardPresenterConfig // get labels here
) {
    companion object {
        fun newInstance(
            context: Context,
            lifecycleOwner: LifecycleOwner,
            niInput: NIInput,
            config: CardPresenterConfig
        ) = CardElementsPresenter(context, lifecycleOwner, niInput, config)
    }

    val onResultSingleLiveEvent = SingleLiveEvent<SuccessErrorResponse>()
    val copiedTextMessageSingleLiveEvent = SingleLiveEvent<Int>()

    private lateinit var cardDetailsClear: CardDetailsModel
    private lateinit var cardDetailsMasked: CardDetailsModel
    private lateinit var clearPanNonSpaced: String

    var isFetchRequested = false
        private set

    val cardNumber: CardElement by lazy {
        makeCardElement(CardMaskableElement.CARDNUMBER)
    }
    val cardHolder: CardElement by lazy {
        makeCardElement(CardMaskableElement.CARDHOLDER)
    }
    val cardExpiry: CardElement by lazy {
        makeCardElement(CardMaskableElement.EXPIRY)
    }
    val cardCvv: CardElement by lazy {
        makeCardElement(CardMaskableElement.CVV)
    }

    fun fetch() {
        isFetchRequested = true
        fetchOnInternetConnectivity()
    }
    /// connectionLiveData.hasInternetConnectivity
    private fun fetchOnInternetConnectivity() {
        lifecycleOwner.lifecycleScope.launch {
//            if (!connectionLiveData.hasInternetConnectivity) {
//                onResultSingleLiveEvent.value = SuccessErrorResponse(
//                    isSuccess = null,
//                    isError = NIErrorResponse(error = NISDKErrors.NETWORK_ERROR)
//                )
//                isFetchRequested = false
//                return@launch
//            }

            val getCardDetailsCoreComponent: IGetCardDetailsCore = GetCardDetailsCoreComponent.fromFactory(niInput)
            val result = getCardDetailsCoreComponent.makeNetworkRequest()
            result.details?.let {
                cardDetailsClear = it.asClearViewModel()
                cardDetailsMasked = it.asMaskedViewModel()
                clearPanNonSpaced = it.asClearPanNonSpaced()
                toggleDetails(false, CardMaskableElementEntries.all()) // unmask all
                toggleDetails(true, config.shouldBeMaskedDefault) // mask selected
            }
            if (result.details == null) {
                isFetchRequested = false
            }
            onResultSingleLiveEvent.value = result.asSuccessErrorResponse()
        }
    }

    fun toggleDetails(isMasked: Boolean, targets: List<CardMaskableElement>) {
        if (!isFetchRequested) {
            return
        }
        for (target in targets) {
            when(target){
                CardMaskableElement.CARDNUMBER -> {
                    cardNumber.updateData(if (isMasked) cardDetailsMasked.pan else cardDetailsClear.pan)
                }

                CardMaskableElement.CARDHOLDER -> {
                    cardHolder.updateData(if (isMasked) cardDetailsMasked.cardholderName else cardDetailsClear.cardholderName)
                }

                CardMaskableElement.EXPIRY -> {
                    cardExpiry.updateData(if (isMasked) cardDetailsMasked.expiry else cardDetailsClear.expiry)
                }

                CardMaskableElement.CVV -> {
                    cardCvv.updateData(if (isMasked) cardDetailsMasked.cVV2 else cardDetailsClear.cVV2)
                }
            }
        }
    }

    fun copyToClipboard(targets: List<CardMaskableElement>, template: String?, clipboardManager: ClipboardManager, @StringRes resId: Int) {
        if (!isFetchRequested) {
            return
        }
        var values = listOf<String>()
        for (target in targets) {
            values = when(target){
                CardMaskableElement.CARDNUMBER -> {
                    values + clearPanNonSpaced
                }

                CardMaskableElement.CARDHOLDER -> {
                    values + cardDetailsClear.cardholderName.toString()
                }

                CardMaskableElement.EXPIRY -> {
                    values + cardDetailsClear.expiry.toString()
                }

                CardMaskableElement.CVV -> {
                    values + cardDetailsClear.cVV2.toString()
                }
            }
        }
        val fallbackTemplate = values.map { "%s" }.joinToString(separator = "\n")
        val result = String.format(template ?: fallbackTemplate, *values.toTypedArray())
        val clip: ClipData = ClipData.newPlainText("", result)
        clipboardManager.setPrimaryClip(clip)

        // Only show a toast for Android 12 and lower.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            copiedTextMessageSingleLiveEvent.value = resId
        }
    }


    private fun makeCardElement(elm: CardMaskableElement): CardElement {
        return CardElement(
            context,
            titleAppearanceRes(elm),
            dataAppearanceRes(elm),
            titleText(elm)
        )
    }

    private fun titleAppearanceRes(elm: CardMaskableElement): Int? {
        return elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> config.cardNumber?.titleAppearanceResId
                CardMaskableElement.EXPIRY -> config.expiry?.titleAppearanceResId
                CardMaskableElement.CARDHOLDER -> config.cardHolder?.titleAppearanceResId
                CardMaskableElement.CVV -> config.cvv?.titleAppearanceResId
            }
        }
    }

    private fun dataAppearanceRes(elm: CardMaskableElement): Int? {
        return elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> config.cardNumber?.dataAppearanceResId
                CardMaskableElement.EXPIRY -> config.expiry?.dataAppearanceResId
                CardMaskableElement.CARDHOLDER -> config.cardHolder?.dataAppearanceResId
                CardMaskableElement.CVV -> config.cvv?.dataAppearanceResId
            }
        }
    }
    private fun titleText(elm: CardMaskableElement): UIElementText? {
        return elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> config.cardNumber?.text
                CardMaskableElement.EXPIRY -> config.expiry?.text
                CardMaskableElement.CARDHOLDER -> config.cardHolder?.text
                CardMaskableElement.CVV -> config.cvv?.text
            }
        }
    }
}
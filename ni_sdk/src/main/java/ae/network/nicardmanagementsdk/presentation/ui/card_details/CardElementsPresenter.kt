package ae.network.nicardmanagementsdk.presentation.ui.card_details

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.CardPresenterConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.core.IGetCardDetailsCore
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentViewModel
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat

class CardElement(
    private val context: Context,
    @StyleRes private val titleAppearanceResId: Int,
    @StyleRes private val dataAppearanceResId: Int,
    @StringRes private val titleRes: Int,
) {
    val title: View by lazy {
        makeTextView(titleAppearanceResId, context, titleRes)
    }

    val data: View by lazy {
        makeTextView(dataAppearanceResId, context, null)
    }

    fun updateData(txt: String) {
        (data as TextView).text = txt
    }
    private fun makeLayoutParams(): ViewGroup.LayoutParams {
        // probably views should live in FrameLayout
        return FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun makeTextView(@StyleRes textAppearance: Int, context: Context, @StringRes textRes: Int?): TextView {
        val textView = TextView(context)
        textView.id = ViewCompat.generateViewId() // MUST have valid id for constraints
        textView.layoutParams = makeLayoutParams()
        textView.setTextAppearance(textAppearance)
        if (textRes != null) {
            textView.setText(textRes)
        }
        return textView
    }
}
class CardElementsPresenter(
    private val context: Context,
    private val viewModel: CardDetailsFragmentViewModel,
    private val config: CardPresenterConfig // get labels here
) {

    var isFetched = false
        private set
//    val factory = Injector.getInstance(requireActivity()).provideCardDetailsFragmentViewModelFactory(niInput)
//    viewModel = ViewModelProvider(this, factory)[CardDetailsFragmentViewModel::class.java]

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
        viewModel.getData()
    }



    private fun makeCardElement(elm: CardMaskableElement): CardElement {
        return CardElement(
            context,
            titleAppearanceRes(elm),
            dataAppearanceRes(elm),
            titleRes(elm, config)
        )
    }

    private fun titleAppearanceRes(elm: CardMaskableElement): Int {
        val configRes = elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> config.cardNumber?.titleAppearanceResId
                CardMaskableElement.EXPIRY -> config.expiry?.titleAppearanceResId
                CardMaskableElement.CARDHOLDER -> config.cardHolder?.titleAppearanceResId
                CardMaskableElement.CVV -> config.cvv?.titleAppearanceResId
            }
        }
        if (configRes != null) {
            return configRes
        }
        val defaultRes = elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberLabel
                CardMaskableElement.EXPIRY -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardExpiryLabel
                CardMaskableElement.CARDHOLDER -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardHolderLabel
                CardMaskableElement.CVV -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardCvvLabel
            }
        }
        return defaultRes
    }

    private fun dataAppearanceRes(elm: CardMaskableElement): Int {
        val configRes = elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> config.cardNumber?.dataAppearanceResId
                CardMaskableElement.EXPIRY -> config.expiry?.dataAppearanceResId
                CardMaskableElement.CARDHOLDER -> config.cardHolder?.dataAppearanceResId
                CardMaskableElement.CVV -> config.cvv?.dataAppearanceResId
            }
        }
        if (configRes != null) {
            return configRes
        }
        val defaultRes = elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberData
                CardMaskableElement.EXPIRY -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardExpiryData
                CardMaskableElement.CARDHOLDER -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardHolderData
                CardMaskableElement.CVV -> R.style.TextAppearance_NICardManagementSDK_CardElement_CardCvvData
            }
        }
        return defaultRes
    }
    private fun titleRes(elm: CardMaskableElement, config: CardPresenterConfig): Int {
        val configRes = elm.let {
            when(elm){
                CardMaskableElement.CARDNUMBER -> config.cardNumber?.labelResource
                CardMaskableElement.EXPIRY -> config.expiry?.labelResource
                CardMaskableElement.CARDHOLDER -> config.cardHolder?.labelResource
                CardMaskableElement.CVV -> config.cvv?.labelResource
            }
        }
        if (configRes != null) {
            return configRes
        }

        val defaultRes = elm.let {
            val shouldDefaultLanguage = config.shouldDefaultLanguage
            when(elm){
                CardMaskableElement.CARDNUMBER -> if (shouldDefaultLanguage) R.string.card_number_en else R.string.card_number_ar
                CardMaskableElement.EXPIRY -> if (shouldDefaultLanguage) R.string.card_expiry_en else R.string.card_expiry_ar
                CardMaskableElement.CARDHOLDER -> if (shouldDefaultLanguage) R.string.card_name_en else R.string.card_name_ar
                CardMaskableElement.CVV -> if (shouldDefaultLanguage) R.string.card_cvv_en else R.string.card_cvv_ar
            }
        }

        return defaultRes
    }
}
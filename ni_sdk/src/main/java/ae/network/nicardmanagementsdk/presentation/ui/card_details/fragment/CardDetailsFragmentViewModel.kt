package ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.interfaces.asSuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.output.asClearPanNonSpaced
import ae.network.nicardmanagementsdk.api.models.output.asClearViewModel
import ae.network.nicardmanagementsdk.api.models.output.asMaskedViewModel
import ae.network.nicardmanagementsdk.core.IGetCardDetailsCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.components.SingleLiveEvent
import ae.network.nicardmanagementsdk.presentation.models.CardDetailsModel
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseViewModel
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// strange but cannot use here either companion object or .entries
enum class CardMaskableElement {
    CARDNUMBER, EXPIRY, CVV, CARDHOLDER,
}

class CardMaskableElementEntries {
    companion object {
        fun all() = listOf<CardMaskableElement>(
            CardMaskableElement.CARDNUMBER,
            CardMaskableElement.EXPIRY,
            CardMaskableElement.CVV,
            CardMaskableElement.CARDHOLDER,
        )
    }
}




class CardDetailsFragmentViewModel(
    private val getCardDetailsCoreComponent: IGetCardDetailsCore
) : ViewModel() {

    private lateinit var cardDetailsClear: CardDetailsModel
    private lateinit var cardDetailsMasked: CardDetailsModel
    private lateinit var clearPanNonSpaced: String

    val onResultSingleLiveEvent = SingleLiveEvent<SuccessErrorResponse>()
    val copiedTextMessageSingleLiveEvent = SingleLiveEvent<Int>()

    val maskedElementsLiveData = MutableLiveData<List<CardMaskableElement>>()
    var shouldBeMaskedDefault: List<CardMaskableElement> = CardMaskableElementEntries.all().toMutableList()


    val cardDetailsLiveData: LiveData<CardDetailsModel> = Transformations.map(maskedElementsLiveData) {
        it?.let {
            if (it.containsAll(CardMaskableElementEntries.all())) {
                cardDetailsMasked
            } else if (it.isEmpty()) {
                cardDetailsClear
            } else {
                CardDetailsModel(
                    pan = if(it.contains(CardMaskableElement.CARDNUMBER)) cardDetailsMasked.pan else cardDetailsClear.pan,
                    expiry = if(it.contains(CardMaskableElement.EXPIRY)) cardDetailsMasked.expiry else cardDetailsClear.expiry,
                    cVV2 = if(it.contains(CardMaskableElement.CVV)) cardDetailsMasked.cVV2 else cardDetailsClear.cVV2,
                    cardholderName = if(it.contains(CardMaskableElement.CARDHOLDER)) cardDetailsMasked.cardholderName else cardDetailsClear.cardholderName
                )
            }
        }
    }

    fun hideShowDetails(targets: List<CardMaskableElement>) {
        val currentMasked = maskedElementsLiveData.value ?: return
        var anyTargetCurrentlyMasked = false
        for (target in targets) {
            if (currentMasked.contains(target)) {
                anyTargetCurrentlyMasked = true
                break
            }
        }
        val filtered = currentMasked.filter { !targets.contains(it) }
        if (anyTargetCurrentlyMasked) { // unmask all targets
            maskedElementsLiveData.value = filtered
        } else { // mask all targets
            maskedElementsLiveData.value = (targets + filtered)
        }
    }

    fun copyToClipboard(targets: List<CardMaskableElement>, template: String?, clipboardManager: ClipboardManager, @StringRes resId: Int) {
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

    fun fetchDataInitially() {
        if (maskedElementsLiveData.value != null) {
            return
        }
        viewModelScope.launch {
            val result = getCardDetailsCoreComponent.makeNetworkRequest()
            result.details?.let {
                cardDetailsClear = it.asClearViewModel()
                cardDetailsMasked = it.asMaskedViewModel()
                clearPanNonSpaced = it.asClearPanNonSpaced()
                maskedElementsLiveData.value = shouldBeMaskedDefault
            }
            onResultSingleLiveEvent.value = result.asSuccessErrorResponse()
        }
    }

}

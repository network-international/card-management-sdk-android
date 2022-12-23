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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CardDetailsFragmentViewModel(
    private val getCardDetailsCoreComponent: IGetCardDetailsCore,
    private val connectionLiveData: IConnection<ConnectionModel>
) : BaseViewModel() {

    private lateinit var cardDetailsClear: CardDetailsModel
    private lateinit var cardDetailsMasked: CardDetailsModel
    private lateinit var clearPanNonSpaced: String

    val getClearPanNonSpaced: String
        get() = clearPanNonSpaced

    val getClearCardHolderName: String
        get() = cardDetailsClear.cardholderName.toString()

    var defaultShouldDisplayValue = false
    val isShowDetailsLiveData = MutableLiveData<Boolean>()
    val cardDetailsLiveData: LiveData<CardDetailsModel> = Transformations.map(isShowDetailsLiveData) {
        it?.let {
            if (it) cardDetailsClear else cardDetailsMasked
        }
    }

    val onResultSingleLiveEvent = SingleLiveEvent<SuccessErrorResponse>()
    val copiedTextMessageSingleLiveEvent = SingleLiveEvent<Int>()

    fun copyToClipboard(text: String, clipboardManager: ClipboardManager, @StringRes resId: Int) {
        val clip: ClipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clip)

        // Only show a toast for Android 12 and lower.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            copiedTextMessageSingleLiveEvent.value = resId
        }
    }

    fun getData() {
        viewModelScope.launch {
            if (connectionLiveData.hasInternetConnectivity) {
                isVisibleProgressBar.value = true
                val result = getCardDetailsCoreComponent.makeNetworkRequest()
                isVisibleProgressBar.value = false
                result.details?.let {
                    cardDetailsClear = it.asClearViewModel()
                    cardDetailsMasked = it.asMaskedViewModel()
                    clearPanNonSpaced = it.asClearPanNonSpaced()
                    isShowDetailsLiveData.value = defaultShouldDisplayValue
                }
                onResultSingleLiveEvent.value = result.asSuccessErrorResponse()
            }
        }
    }
}

package ae.network.nicardmanagementsdk.presentation.ui.verify_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModelBase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VerifyPinViewModel (
    private val setVerifyPinCore: ISetVerifyPinCore,
    private val connectionLiveData: IConnection<ConnectionModel>,
    private val niInput : NIInput
) : SetPinViewModelBase() {

    override val navTitle = MutableLiveData(getNavTitle())
    override val screenTitle = MutableLiveData(getScreenTitle())


    override fun onDoneImageButtonTap() {
        viewModelScope.launch {
            if (connectionLiveData.hasInternetConnectivity) {
                isVisibleProgressBar.value = true
                val result = setVerifyPinCore.makeNetworkRequest(inputString)
                isVisibleProgressBar.value = false
                onResultSingleLiveEvent.value = result
            }
        }
    }

    private fun getNavTitle(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.verify_pin_title_ar
            else -> R.string.verify_pin_title_en
        }

    private fun getScreenTitle(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.verify_pin_description_ar
            else -> R.string.verify_pin_description_en
        }
}
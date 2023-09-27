package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import androidx.lifecycle.MutableLiveData

class SetPinViewModel (
    private val setVerifyPinCore: ISetVerifyPinCore,
    connectionLiveData: IConnection<ConnectionModel>,
    private val niInput: NIInput
) : SetPinTwoStepViewModelBase(connectionLiveData) {

    override val navTitle = MutableLiveData(getNavTitle())
    override val screenTitle = MutableLiveData(getScreenTitle())

    override fun onDoneImageButtonTap() {
        setPinTwoSteps(networkRequest = { setVerifyPinCore.makeNetworkRequest(secondTimePinValue) })
    }

    private fun getNavTitle(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.set_pin_title_ar
            else -> R.string.set_pin_title_en
        }

    private fun getScreenTitle(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.set_pin_description_enter_pin_ar
            else -> R.string.set_pin_description_enter_pin_en
        }
}
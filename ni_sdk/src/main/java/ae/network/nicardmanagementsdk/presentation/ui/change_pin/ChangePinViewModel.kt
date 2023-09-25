package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.core.IChangePinCore
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinTwoStepViewModelBase
import androidx.lifecycle.MutableLiveData

class ChangePinViewModel(
    private val changePinCoreComponent: IChangePinCore,
    connectionLiveData: IConnection<ConnectionModel>,
    private val niInput : NIInput
) : SetPinTwoStepViewModelBase(connectionLiveData) {

    override val navTitle = MutableLiveData(getNavTitle())
    override val screenTitle = MutableLiveData(getScreenTitle())

    private var currentPin = ""
    private var isCurrentPinSetup = true

    override fun onDoneImageButtonTap() {
        if (isCurrentPinSetup) {
            captureCurrentPin()
        } else {
            setPinTwoSteps(networkRequest = {
                changePinCoreComponent.makeNetworkRequest(
                    currentPin,
                    secondTimePinValue
                )
            })
        }
    }

    private fun captureCurrentPin() {
        currentPin = inputString
        resetState()
        screenTitle.value = getNewPinScreenTitle()
        isCurrentPinSetup = false
    }

    private fun getNavTitle(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.change_pin_title_ar
            else -> R.string.change_pin_title_en
        }

    private fun getScreenTitle(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.change_pin_description_enter_current_pin_ar
            else -> R.string.change_pin_description_enter_current_pin_en
        }

    private fun getNewPinScreenTitle(): Int =
        when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> R.string.change_pin_description_enter_new_pin_ar
            else -> R.string.change_pin_description_enter_new_pin_en
        }
}
package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.api.implementation.NICardManagement
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModelBase

class ChangePinViewModel(
    private val niInput: NIInput,
    navTitleText: UIElementText,
    screenTitleText: UIElementText,
    private val newPinTitleText: UIElementText,
    approvePinTitleText: UIElementText,
    notMatchTitleText: UIElementText,
) : SetPinViewModelBase(navTitleText, screenTitleText, approvePinTitleText, notMatchTitleText) {

    private var currentPin = ""
    private var isCurrentPinSetup = true

    override fun onDoneImageButtonTap() {
        if (isCurrentPinSetup) {
            currentPin = inputString
            resetState()
            screenTitle.value = newPinTitleText
            isCurrentPinSetup = false
        } else {
            setPinTwoSteps(networkRequest = {
                NICardManagement.changePin(currentPin, secondTimePinValue, niInput)
            })
        }
    }
}

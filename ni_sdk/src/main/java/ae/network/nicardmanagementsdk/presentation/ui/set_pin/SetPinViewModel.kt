package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.api.implementation.NICardManagement
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.UIElementText

class SetPinViewModel (
    private val niInput: NIInput,
    navTitleText: UIElementText,
    screenTitleText: UIElementText,
    secondStepTitleText: UIElementText,
    notMatchTitleText: UIElementText,
) : SetPinViewModelBase(navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText) {

    override fun onDoneImageButtonTap() {
        setPinTwoSteps(networkRequest = { NICardManagement.setPin(secondTimePinValue, niInput) })
    }
}

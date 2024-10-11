package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.core.IChangePinCore
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModelBase
import androidx.lifecycle.MutableLiveData

class ChangePinViewModel(
    private val changePinCoreComponent: IChangePinCore,
    private val navTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_title_en)
    private val screenTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_description_enter_current_pin_en)
    private val newPinTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_description_enter_new_pin_en)
    private val approvePinTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
    private val notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en
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
                changePinCoreComponent.makeNetworkRequest(
                    currentPin,
                    secondTimePinValue
                )
            })
        }
    }
}
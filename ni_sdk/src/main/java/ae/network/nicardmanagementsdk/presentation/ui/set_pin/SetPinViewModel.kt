package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore

class SetPinViewModel (
    private val setVerifyPinCore: ISetVerifyPinCore,
    private val navTitleText: UIElementText, // = UIElementText.Int(R.string.set_pin_title_en)
    private val screenTitleText: UIElementText, // = UIElementText.Int(R.string.set_pin_description_enter_pin_en)
    private val secondStepTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
    private val notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en
) : SetPinViewModelBase(navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText) {



    override fun onDoneImageButtonTap() {
        setPinTwoSteps(networkRequest = { setVerifyPinCore.makeNetworkRequest(secondTimePinValue) })
    }
}
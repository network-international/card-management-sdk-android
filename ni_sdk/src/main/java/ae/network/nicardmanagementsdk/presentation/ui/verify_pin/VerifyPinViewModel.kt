package ae.network.nicardmanagementsdk.presentation.ui.verify_pin

import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModelBase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VerifyPinViewModel (
    private val setVerifyPinCore: ISetVerifyPinCore,
    private val navTitleText: UIElementText, // = UIElementText.Int(R.string.verify_pin_title_en)
    private val screenTitleText: UIElementText, // = UIElementText.Int(R.string.verify_pin_description_en)
    // just for base model
    private val secondStepTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
    private val notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en
) : SetPinViewModelBase(navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText) {

    override fun onDoneImageButtonTap() {
        viewModelScope.launch {
            isVisibleProgressBar.value = true
            val result = setVerifyPinCore.makeNetworkRequest(inputString)
            isVisibleProgressBar.value = false
            onResultSingleLiveEvent.value = result
        }
    }
}
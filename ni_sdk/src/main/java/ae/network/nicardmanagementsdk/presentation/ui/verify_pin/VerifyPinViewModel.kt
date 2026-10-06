package ae.network.nicardmanagementsdk.presentation.ui.verify_pin

import ae.network.nicardmanagementsdk.api.implementation.NICardManagement
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModelBase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VerifyPinViewModel (
    private val niInput: NIInput,
    navTitleText: UIElementText,
    screenTitleText: UIElementText,
    secondStepTitleText: UIElementText,
    notMatchTitleText: UIElementText,
) : SetPinViewModelBase(navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText) {

    override fun onDoneImageButtonTap() {
        viewModelScope.launch {
            isVisibleProgressBar.value = true
            val result = NICardManagement.verifyPin(inputString, niInput)
            isVisibleProgressBar.value = false
            onResultSingleLiveEvent.value = result
        }
    }
}

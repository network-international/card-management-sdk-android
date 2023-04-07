package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SetPinViewModel (
    private val setVerifyPinCore: ISetVerifyPinCore,
    private val connectionLiveData: IConnection<ConnectionModel>
) : SetPinViewModelBase() {

    override val navTitle = MutableLiveData(R.string.set_pin_title)
    override val screenTitle = MutableLiveData(R.string.set_pin_description_enter_pin)

    private var firstTimePinValue = ""
    private var secondTimePinValue = ""
    private var isStepOnePinSetup = true

    override fun onDoneImageButtonTap() {
        if (isStepOnePinSetup) {
            firstTimePinValue = inputString
            resetState()
            screenTitle.value = R.string.set_pin_description_re_enter_pin
            isStepOnePinSetup = false
        } else {
            secondTimePinValue = inputString
            if (firstTimePinValue == secondTimePinValue) {
                viewModelScope.launch {
                    if (connectionLiveData.hasInternetConnectivity) {
                        isVisibleProgressBar.value = true
                        val result = setVerifyPinCore.makeNetworkRequest(inputString)
                        isVisibleProgressBar.value = false
                        onResultSingleLiveEvent.value = result
                    }
                }
            } else {
                screenTitle.value = R.string.set_pin_description_pin_not_match
                resetStepTwoPinState()
            }
        }
    }

    private fun resetStepTwoPinState() {
        secondTimePinValue = ""
        resetState()
    }
}
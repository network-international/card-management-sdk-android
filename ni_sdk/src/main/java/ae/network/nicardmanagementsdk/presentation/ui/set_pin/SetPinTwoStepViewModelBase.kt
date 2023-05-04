package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class SetPinTwoStepViewModelBase(
    private val connectionLiveData: IConnection<ConnectionModel>
    ) : SetPinViewModelBase() {

    private var firstTimePinValue = ""
    protected var secondTimePinValue = ""
    private var isStepOnePinSetup = true

    private fun resetStepTwoPinState() {
        secondTimePinValue = ""
        resetState()
    }

    protected fun setPinTwoSteps(
        networkRequest: suspend () -> SuccessErrorResponse
    ) {
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
                        val result = networkRequest()
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
}
package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.core.IChangePinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinTwoStepViewModelBase
import androidx.lifecycle.MutableLiveData

class ChangePinViewModel(
    private val changePinCoreComponent: IChangePinCore,
    connectionLiveData: IConnection<ConnectionModel>
) : SetPinTwoStepViewModelBase(connectionLiveData) {

    override val navTitle = MutableLiveData(R.string.change_pin_title)
    override val screenTitle = MutableLiveData(R.string.change_pin_description_enter_current_pin)

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
        screenTitle.value = R.string.change_pin_description_enter_new_pin
        isCurrentPinSetup = false
    }
}
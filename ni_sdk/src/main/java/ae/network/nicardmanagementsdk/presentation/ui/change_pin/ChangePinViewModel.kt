package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.core.IChangePinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModelBase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChangePinViewModel (
    private val changePinCoreComponent: IChangePinCore,
    private val connectionLiveData: IConnection<ConnectionModel>
) : SetPinViewModelBase() {

    override val navTitle = MutableLiveData(R.string.change_pin_title)
    override val screenTitle = MutableLiveData(R.string.change_pin_description_step1)
    private var oldPin = ""
    private var newPin = ""
    private var isCurrentPinSetup = true


    override fun onDoneImageButtonTap() {
        if (isCurrentPinSetup) {
            oldPin = inputString
            resetState()
            screenTitle.value = R.string.change_pin_description_step2
            isCurrentPinSetup = false
        } else {
            newPin = inputString
            viewModelScope.launch {
                if (connectionLiveData.hasInternetConnectivity) {
                    isVisibleProgressBar.value = true
                    val result = changePinCoreComponent.makeNetworkRequest(
                        oldPin,
                        newPin
                    )
                    isVisibleProgressBar.value = false
                    onResultSingleLiveEvent.value = result
                }
            }
        }
    }

    fun resetChangePinState() {
        oldPin = ""
        newPin = ""
        isCurrentPinSetup = true
        screenTitle.value = R.string.change_pin_description_step1
        resetState()
    }

}
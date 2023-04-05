package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class SetVerifyPinBaseViewModel (
    private val iSetVerifyPinCore: ISetVerifyPinCore,
    private val connectionLiveData: IConnection<ConnectionModel>
) : SetPinViewModelBase() {

    override fun onDoneImageButtonTap() {
        viewModelScope.launch {
            if (connectionLiveData.hasInternetConnectivity) {
                isVisibleProgressBar.value = true
                val result = iSetVerifyPinCore.makeNetworkRequest(inputString)
                isVisibleProgressBar.value = false
                onResultSingleLiveEvent.value = result
            }
        }
    }

}
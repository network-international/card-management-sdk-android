package ae.network.nicardmanagementsdk.presentation.ui.verify_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModelBase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VerifyPinViewModel (
    private val setVerifyPinCore: ISetVerifyPinCore,
    private val connectionLiveData: IConnection<ConnectionModel>
) : SetPinViewModelBase() {

    override val navTitle = MutableLiveData(R.string.verify_pin_title)
    override val screenTitle = MutableLiveData(R.string.verify_pin_description)

    override fun onDoneImageButtonTap() {
        viewModelScope.launch {
            if (connectionLiveData.hasInternetConnectivity) {
                isVisibleProgressBar.value = true
                val result = setVerifyPinCore.makeNetworkRequest(inputString)
                isVisibleProgressBar.value = false
                onResultSingleLiveEvent.value = result
            }
        }
    }
}
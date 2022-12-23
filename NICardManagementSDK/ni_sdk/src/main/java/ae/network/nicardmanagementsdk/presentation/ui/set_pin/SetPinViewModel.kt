package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.core.ISetPinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SetPinViewModel (
    private val setPinCoreComponent: ISetPinCore,
    private val connectionLiveData: IConnection<ConnectionModel>
) : SetPinViewModelBase() {

    override val navTitle = MutableLiveData(R.string.set_pin)
    override val screenTitle = MutableLiveData(R.string.set_pin_screen_title)


    override fun onDoneImageButtonTap() {
        viewModelScope.launch {
            if (connectionLiveData.hasInternetConnectivity) {
                isVisibleProgressBar.value = true
                val result = setPinCoreComponent.makeNetworkRequest(inputString)
                isVisibleProgressBar.value = false
                onResultSingleLiveEvent.value = result
            }
        }
    }

}
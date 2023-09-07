package ae.network.nicardmanagementsdk.presentation.ui.view_pin

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.interfaces.ViewPinErrorResponse
import ae.network.nicardmanagementsdk.api.interfaces.asSuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.output.ViewPinResponse
import ae.network.nicardmanagementsdk.api.models.output.asClearViewModel
import ae.network.nicardmanagementsdk.api.models.output.asMaskedViewModel
import ae.network.nicardmanagementsdk.core.IViewPinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.components.SingleLiveEvent
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewPinFragmentViewModel(
    private val viewPinCore: IViewPinCore,
    private val connectionLiveData: IConnection<ConnectionModel>
) : BaseViewModel() {

    val onResultSingleLiveEvent = SingleLiveEvent<SuccessErrorResponse>()

    private val pinClearLiveData = MutableLiveData<String>()
    private val pinMaskedLiveData = MutableLiveData<String>()

    val getPinClearLiveData: LiveData<String>
        get() = pinClearLiveData

    val getPinMaskedLiveData: LiveData<String>
        get() = pinMaskedLiveData

    val hasPinData = MutableLiveData<Boolean>()

    fun getPin() {
        viewModelScope.launch {
            if (connectionLiveData.hasInternetConnectivity) {
                isVisibleProgressBar.value = true
                val result = viewPinCore.makeNetworkRequest()
                isVisibleProgressBar.value = false
                if (result.pin != null) {
                    val pinClear = result.pin.asClearViewModel().encryptedPin
                    val pinMasked = result.pin.asMaskedViewModel().encryptedPin
                    pinClearLiveData.value = pinClear
                    pinMaskedLiveData.value = pinMasked
                    onResultSingleLiveEvent.value = result.asSuccessErrorResponse()
                    hasPinData.value = true
                } else {
                    Log.d("ViewPinViewModel::", "result.pin is null")
                }
            }
        }
    }
}
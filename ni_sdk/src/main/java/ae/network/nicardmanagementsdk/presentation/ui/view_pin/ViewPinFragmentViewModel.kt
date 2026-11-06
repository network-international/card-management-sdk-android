package ae.network.nicardmanagementsdk.presentation.ui.view_pin

import ae.network.nicardmanagementsdk.api.implementation.NICardManagement
import ae.network.nicardmanagementsdk.api.interfaces.asSuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.presentation.components.SingleLiveEvent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewPinFragmentViewModel(
    private val niInput: NIInput,
    val timerStringTemplate: UIElementText
) : ViewModel() {
    val isVisibleProgressBar = MutableLiveData(false)

    val onResultSingleLiveEvent = SingleLiveEvent<ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse>()

    private val pinClearLiveData = MutableLiveData<String>()
    private val pinMaskedLiveData = MutableLiveData<String>()

    val getPinClearLiveData: LiveData<String>
        get() = pinClearLiveData

    val getPinMaskedLiveData: LiveData<String>
        get() = pinMaskedLiveData

    val hasPinData = MutableLiveData<Boolean>()

    fun getPin() {
        viewModelScope.launch {
            isVisibleProgressBar.value = true
            val result = NICardManagement.getPin(niInput)
            isVisibleProgressBar.value = false
            if (result.pin != null) {
                val pinClear = result.pin!!.pin
                pinClearLiveData.value = pinClear
                pinMaskedLiveData.value = "*".repeat(pinClear.length)
                onResultSingleLiveEvent.value = result.asSuccessErrorResponse()
                hasPinData.value = true
            } else if (result.error != null) {
                onResultSingleLiveEvent.value = result.asSuccessErrorResponse()
                hasPinData.value = false
            } else {
                hasPinData.value = false
                Log.d("ViewPinViewModel::", "result.pin is null")
            }
        }
    }
}

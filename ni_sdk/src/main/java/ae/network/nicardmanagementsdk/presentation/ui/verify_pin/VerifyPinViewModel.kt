package ae.network.nicardmanagementsdk.presentation.ui.verify_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetVerifyPinBaseViewModel
import androidx.lifecycle.MutableLiveData

class VerifyPinViewModel (
    setVerifyPinCore: ISetVerifyPinCore,
    connectionLiveData: IConnection<ConnectionModel>
) : SetVerifyPinBaseViewModel(setVerifyPinCore, connectionLiveData) {

    override val navTitle = MutableLiveData(R.string.verify_pin)
    override val screenTitle = MutableLiveData(R.string.verify_pin_screen_title)
}
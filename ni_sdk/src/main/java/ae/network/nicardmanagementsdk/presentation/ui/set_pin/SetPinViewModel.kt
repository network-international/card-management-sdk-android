package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.core.ISetVerifyPinCore
import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import androidx.lifecycle.MutableLiveData

class SetPinViewModel (
    setVerifyPinCore: ISetVerifyPinCore,
    connectionLiveData: IConnection<ConnectionModel>
) : SetVerifyPinBaseViewModel(setVerifyPinCore, connectionLiveData) {

    override val navTitle = MutableLiveData(R.string.set_pin_title)
    override val screenTitle = MutableLiveData(R.string.set_pin_description)
}
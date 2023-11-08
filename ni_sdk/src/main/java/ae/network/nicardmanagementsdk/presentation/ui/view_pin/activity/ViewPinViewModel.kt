package ae.network.nicardmanagementsdk.presentation.ui.view_pin.activity

import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseViewModel

class ViewPinViewModel(
    private val connectionLiveData: IConnection<ConnectionModel>
) : BaseViewModel()
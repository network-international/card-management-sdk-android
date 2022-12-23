package ae.network.nicardmanagementsdk.presentation.ui.card_details

import ae.network.nicardmanagementsdk.network.utils.ConnectionModel
import ae.network.nicardmanagementsdk.network.utils.IConnection
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseViewModel

class CardDetailsViewModel(
    private val connectionLiveData: IConnection<ConnectionModel>
) : BaseViewModel() {

}
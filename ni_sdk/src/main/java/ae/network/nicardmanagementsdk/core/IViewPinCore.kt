package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.ViewPinErrorResponse

interface IViewPinCore {
    suspend fun makeNetworkRequest(): ViewPinErrorResponse
}

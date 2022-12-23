package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse

interface ISetPinCore {
    suspend fun makeNetworkRequest(
        pin: String
    ): SuccessErrorResponse
}

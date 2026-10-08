package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse

interface ISetVerifyPinCore {
    suspend fun makeNetworkRequest(
        pin: String
    ): SuccessErrorResponse
}

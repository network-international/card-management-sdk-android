package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse

interface IChangePinCore {
    suspend fun makeNetworkRequest(
        oldPin: String,
        newPin: String
    ): SuccessErrorResponse
}
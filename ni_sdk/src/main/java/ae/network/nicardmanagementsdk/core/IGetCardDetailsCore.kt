package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.DetailsErrorResponse

interface IGetCardDetailsCore {
    suspend fun makeNetworkRequest(): DetailsErrorResponse
}

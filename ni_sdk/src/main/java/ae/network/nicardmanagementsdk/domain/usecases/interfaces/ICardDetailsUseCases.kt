package ae.network.nicardmanagementsdk.domain.usecases.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NICardDetailsResponse

interface ICardDetailsUseCases {
    suspend fun getSecuredCardDetails(input: NIInput): NICardDetailsResponse
}
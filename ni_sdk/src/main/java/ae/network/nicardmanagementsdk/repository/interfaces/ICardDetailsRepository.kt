package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.CardDetailsResponse

interface ICardDetailsRepository {

    suspend fun getSecuredCardDetails(
        input: NIInput,
        publicKey: String
    ): CardDetailsResponse

}
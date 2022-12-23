package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.DetailsErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NICardDetailsResponse
import ae.network.nicardmanagementsdk.domain.usecases.implementation.CardDetailsUseCases
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ICardDetailsUseCases
import ae.network.nicardmanagementsdk.repository.implementation.CardDetailsRepository
import ae.network.nicardmanagementsdk.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class GetCardDetailsCoreComponent(
    private val cardDetailsUseCase: ICardDetailsUseCases,
    private val input: NIInput
) : IGetCardDetailsCore {

    override suspend fun makeNetworkRequest(): DetailsErrorResponse {
        val response = withContext(Dispatchers.IO) {
            val e: Exception?
            try {
                val securedCardDetails = cardDetailsUseCase.getSecuredCardDetails(input)
                return@withContext DetailsErrorResponse(
                    securedCardDetails,
                    null
                )
            } catch (exception: Exception) {
                e = exception
            }

            // map the "e" exception to NISDKErrors
            return@withContext DetailsErrorResponse.fromException(e!!)
        }

        return response
    }

    companion object {
        fun fromFactory(input: NIInput): GetCardDetailsCoreComponent {
            val network = Network(input.connectionProperties)
            val cardDetailsRepository = CardDetailsRepository(network.cardDetailsApi)
            val cardDetailsUseCases = CardDetailsUseCases(cardDetailsRepository)
            return GetCardDetailsCoreComponent(cardDetailsUseCases, input)
        }
    }
}
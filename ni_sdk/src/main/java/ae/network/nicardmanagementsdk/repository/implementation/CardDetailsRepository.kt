package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.CardDetailsResponse
import ae.network.nicardmanagementsdk.network.dto.card_details.X509CertificateBodyDto
import ae.network.nicardmanagementsdk.network.dto.card_details.asDomainModel
import ae.network.nicardmanagementsdk.network.retrofit_api.CardDetailsApi
import ae.network.nicardmanagementsdk.repository.interfaces.ICardDetailsRepository

const val CHANNEL_ID = "sdk"

class CardDetailsRepository(
    private val cardDetailsApi: CardDetailsApi
    ) : BaseRepository(), ICardDetailsRepository {

    override suspend fun getSecuredCardDetails(
        input: NIInput,
        publicKey: String
    ): CardDetailsResponse {
        return cardDetailsApi.getSecuredCardDetails(
            headerRetrofit(input.connectionProperties.token, input.bankCode),
            input.cardIdentifierId,
            input.cardIdentifierType,
            X509CertificateBodyDto(publicKey)
        ).asDomainModel()
    }
}
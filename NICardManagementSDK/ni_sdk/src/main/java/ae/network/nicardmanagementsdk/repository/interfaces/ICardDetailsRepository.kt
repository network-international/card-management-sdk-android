package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.api.models.output.CardDetailsResponse
import ae.network.nicardmanagementsdk.network.dto.card_details.X509CertificateBodyDto

interface ICardDetailsRepository {

    suspend fun getSecuredCardDetails(
        token: String,
        cardIdentifierId: String,
        cardIdentifierType: String,
        certificate: X509CertificateBodyDto
    ): CardDetailsResponse

}
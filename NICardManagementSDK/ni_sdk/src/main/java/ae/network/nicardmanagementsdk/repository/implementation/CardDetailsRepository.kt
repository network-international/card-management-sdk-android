package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.output.CardDetailsResponse
import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.network.dto.card_details.X509CertificateBodyDto
import ae.network.nicardmanagementsdk.network.dto.card_details.asDomainModel
import ae.network.nicardmanagementsdk.network.retrofit_api.CardDetailsApi
import ae.network.nicardmanagementsdk.repository.interfaces.ICardDetailsRepository

const val FINANCIAL_ID = "EAND"
const val CHANNEL_ID = "sdk"

class CardDetailsRepository(private val cardDetailsApi: CardDetailsApi) : ICardDetailsRepository {

    override suspend fun getSecuredCardDetails(
        token: String,
        cardIdentifierId: String,
        cardIdentifierType: String,
        certificate: X509CertificateBodyDto
    ): CardDetailsResponse {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return cardDetailsApi.getSecuredCardDetails(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", FINANCIAL_ID),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            cardIdentifierId,
            cardIdentifierType,
            certificate
        ).asDomainModel()
    }
}
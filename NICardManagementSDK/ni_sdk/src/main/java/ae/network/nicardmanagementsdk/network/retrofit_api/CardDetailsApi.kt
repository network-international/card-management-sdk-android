package ae.network.nicardmanagementsdk.network.retrofit_api

import ae.network.nicardmanagementsdk.network.dto.card_details.SecuredCardDetailsResponseDto
import ae.network.nicardmanagementsdk.network.dto.card_details.X509CertificateBodyDto
import retrofit2.http.*

interface CardDetailsApi {

    @POST("cards/{id}/secured")
    suspend fun getSecuredCardDetails(
        @HeaderMap headers: Map<String, String>,
        @Path("id") cardIdentifierId: String,
        @Query("card_identifier_type") cardIdentifierType: String,
        @Body certificate: X509CertificateBodyDto
    ): SecuredCardDetailsResponseDto
}
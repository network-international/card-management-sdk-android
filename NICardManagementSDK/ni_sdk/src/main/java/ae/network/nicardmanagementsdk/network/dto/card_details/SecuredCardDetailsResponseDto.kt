package ae.network.nicardmanagementsdk.network.dto.card_details

import ae.network.nicardmanagementsdk.api.models.output.CardDetailsResponse
import ae.network.nicardmanagementsdk.api.models.output.NICardDetailsResponse
import com.google.gson.annotations.SerializedName

data class SecuredCardDetailsResponseDto(
    @SerializedName("encrypted_pan") val encryptedPan : String,
    @SerializedName("masked_pan") val maskedPan : String,
    @SerializedName("expiry") val expiry : String,
    @SerializedName("encrypted_cvv2") val encryptedCvv2 : String,
    @SerializedName("cardholder_name") val cardholderName : String,
    @SerializedName("embossing_line4") val embossingLine4 : String,
    @SerializedName("product_code") val productCode : String,
    @SerializedName("product_short_code") val productShortCode : String,
    @SerializedName("product_name") val productName : String,
    @SerializedName("card_brand") val cardBrand : String
)

fun SecuredCardDetailsResponseDto.asDomainModel() : CardDetailsResponse {
    return CardDetailsResponse(
        encryptedPan,
        maskedPan,
        expiry,
        encryptedCvv2,
        cardholderName
    )
}

package ae.network.nicardmanagementsdk.network.dto.change_pin

import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import com.google.gson.annotations.SerializedName

data class CardIdentifierResponseDto(
    @SerializedName("card_identifier_id") val cardIdentifierId : String,
    @SerializedName("card_identifier_type") val cardIdentifierType : String
)

fun CardIdentifierResponseDto.asDomainModel(): CardIdentifierModel {
    return CardIdentifierModel(
        cardIdentifierId,
        cardIdentifierType
    )
}

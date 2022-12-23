package ae.network.nicardmanagementsdk.network.dto.set_pin

import com.google.gson.annotations.SerializedName

data class CardIdentifierBodyDto(
    @SerializedName("card_identifier_type") val cardIdentifierType : String,
    @SerializedName("card_identifier_id") val cardIdentifierId : String,
    @SerializedName("publicKey") val publicKey : String
)

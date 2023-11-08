package ae.network.nicardmanagementsdk.network.dto.view_pin

import com.google.gson.annotations.SerializedName

data class PINViewBodyDto(
    @SerializedName("card_identifier_type") val cardIdentifierType: String,
    @SerializedName("card_identifier_id") val cardIdentifierId: String,
    @SerializedName("public_key") val publicKey: String
)
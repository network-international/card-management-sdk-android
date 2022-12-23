package ae.network.nicardmanagementsdk.network.dto.set_pin

import com.google.gson.annotations.SerializedName

data class SetPinBodyDto(
    @SerializedName("card_identifier_id") val cardIdentifierId : String,
    @SerializedName("encrypted_pin") val encryptedPin : String,
    @SerializedName("encryption_method") val encryptionMethod : String,
    @SerializedName("card_identifier_type") val cardIdentifierType : String
)

enum class EncryptionMethodEnum {
    ASYMMETRIC_ENC,
    SYMMETRIC_ENC
}

enum class CardIdentifierTypeEnum {
    EXID, CONTRACT_NUMBER
}
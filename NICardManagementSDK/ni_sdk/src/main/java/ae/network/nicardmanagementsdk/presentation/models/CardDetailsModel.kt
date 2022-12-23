package ae.network.nicardmanagementsdk.presentation.models

data class CardDetailsModel(
    val pan: String?,
    val expiry: String?,      // TODO: - TBD if type Date or String
    val cVV2: String?,
    val cardholderName: String?
)

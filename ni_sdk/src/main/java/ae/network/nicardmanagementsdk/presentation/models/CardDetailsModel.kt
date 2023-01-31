package ae.network.nicardmanagementsdk.presentation.models

data class CardDetailsModel(
    val pan: String,
    val expiry: String,
    val cVV2: String,
    val cardholderName: String
)

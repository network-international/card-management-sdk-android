package ae.network.nicardmanagementsdk.api.models.output

import ae.network.nicardmanagementsdk.domain.models.ViewPinModel

data class ViewPinResponse(
    val encrypted_pin: String
)

fun ViewPinResponse.asClearViewModel(): ViewPinModel =
    ViewPinModel(encrypted_pin)

fun ViewPinResponse.asMaskedViewModel(): ViewPinModel =
    ViewPinModel("*".repeat(encrypted_pin.length))
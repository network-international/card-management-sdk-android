package ae.network.nicardmanagementsdk.api.models.input

import androidx.annotation.FontRes
import java.io.Serializable

data class NIFontLabelPair(
    val uiFont: UIFont,
    var label: NILabels
): Serializable

data class UIFont(
    @FontRes
    val fontRes: Int? = null,
    // interpreted as "scaled pixel" units Sp
    val textSize: Int
): Serializable

enum class NILabels: Serializable {

    // Card Details
    CARD_NUMBER_LABEL,
    CARD_NUMBER_VALUE_LABEL,
    EXPIRY_DATE_LABEL,
    EXPIRY_DATE_VALUE_LABEL,
    CVV_LABEL,
    CVV_VALUE_LABEL,
    CARD_HOLDER_NAME_LABEL,

    // Set PIN
    SET_DESCRIPTION_LABEL,

    // Change PIN
    CHANGE_PIN_DESCRIPTION_LABEL
}



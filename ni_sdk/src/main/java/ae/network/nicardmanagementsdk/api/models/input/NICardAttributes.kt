package ae.network.nicardmanagementsdk.api.models.input

import androidx.annotation.DrawableRes
import java.io.Serializable

data class NICardAttributes(
    // if true, the card details will be hidden/masked by default; if false, the card details will be visible by default
    val shouldHide: Boolean = true,
    // if set, this image will be used as background for the card details view; if not set, it will use default image from sdk
    @DrawableRes
    val backgroundImage: Int? = null,
    // if set will apply new text positioning values for Card Details components: cardNumberLine, dateCvvLine and cardHolderNameLine
    // if not set default positioning will be used
    val textPositioning: TextPositioning? = null
): Serializable

// the ratio of the parent container width/height in the range 0..1 (meaning 0 to 100% percent)
// relative to the left|top corner (which is the axis origin point)
// leftAlignment: all three groups of views (lines) have the same left alignment
// cardNumberGroupTopAlignment: card number line top alignment
// dateCvvGroupTopAlignment: date cvv line top alignment
// cardHolderNameGroupTopAlignment: card holder name line top alignment
data class TextPositioning(
    val leftAlignment: Float? = null,
    val cardNumberGroupTopAlignment: Float? = null,
    val dateCvvGroupTopAlignment: Float? = null,
    val cardHolderNameGroupTopAlignment: Float? = null
): Serializable
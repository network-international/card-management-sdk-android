package ae.network.nicardmanagementsdk.api.models.input

import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElementEntries
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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


/// Define visibility of element
/// - always visible
/// - visible when element masked
/// - visible when element unmasked
/// - always invisible
enum class CardElementsVisibilityType {
    VISIBLE_ALWAYS, VISIBLE_MASKED, VISIBLE_UNMASKED, INVISIBLE_ALWAYS
}
data class CardElementLayout(
    val top: Int? = null,
    val bottom: Int? = null,
    val left: Int? = null,
    val right: Int? = null,
): Serializable

// TODO: define interface and several implementations - label / details / button and use suitable configs there
// example: ConstraintInstructions
data class CardElementsItemConfig(
    @StringRes
    val labelResource: Int? = null,
    // Colors
    @ColorRes
    val labelColor: Int? = null,
    @ColorRes
    val detailsColor: Int? = null,
    // Button images
    @DrawableRes
    var copyButtonImage: Int? = null,
    @DrawableRes
    var maskButtonShowImage: Int? = null,
    @DrawableRes
    var maskButtonHideImage: Int? = null,
    // Layout
    val labelLayout: CardElementLayout? = null,
    val detailsLayout: CardElementLayout? = null,
    val copyButtonLayout: CardElementLayout? = null,
    val maskButtonLayout: CardElementLayout? = null,
): Serializable
data class CardElementsConfig(
    val cardNumber: CardElementsItemConfig? = null,
    val expiry: CardElementsItemConfig? = null,
    val cvv: CardElementsItemConfig? = null,
    val cardHolder: CardElementsItemConfig? = null,
    // common mask button - toggle all elements together
    val commonMaskButton: CardElementsItemConfig? = null, // common show details button
    val commonMaskButtonTargets: List<CardMaskableElement> = CardMaskableElementEntries.all(),
    // define initial state of masking
    val shouldBeMaskedDefault: List<CardMaskableElement> = listOf(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.EXPIRY,
        CardMaskableElement.CVV,
        CardMaskableElement.CARDHOLDER,
    ),
    // progressBar - for free form only, if not nil - standard progressIndicator shows progress
    val progressBar: CardElementsItemConfig? = null, // use `details` field for color and layout
): Serializable
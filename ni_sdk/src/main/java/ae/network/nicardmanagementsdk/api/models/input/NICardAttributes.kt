package ae.network.nicardmanagementsdk.api.models.input

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElementEntries
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import java.io.Serializable

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
): Serializable {
    companion object {
        fun default() = CardElementsConfig(
            cardNumber = CardElementsItemConfig(
                labelLayout = CardElementLayout(left = 30, top = 130),
                detailsLayout = CardElementLayout(left = 30, top = 160),
                copyButtonLayout = CardElementLayout(left = 510, top = 170),
                copyButtonImage = R.drawable.ic_baseline_content_copy, // use null to hide button
            ),
            expiry = CardElementsItemConfig(
                labelLayout = CardElementLayout(left = 30, top = 240),
                detailsLayout = CardElementLayout(left = 30, top = 270),
            ),
            cvv = CardElementsItemConfig(
                labelLayout = CardElementLayout(left = 200, top = 240),
                detailsLayout = CardElementLayout(left = 200, top = 270),
            ),
            cardHolder = CardElementsItemConfig(
                labelLayout = CardElementLayout(left = 30, top = 360),
                detailsLayout = CardElementLayout(left = 30, top = 390),
                copyButtonLayout = null,
                copyButtonImage = null
            ),
            commonMaskButton = CardElementsItemConfig(
                maskButtonHideImage = R.drawable.ic_hide_details,
                maskButtonShowImage = R.drawable.ic_reveal_details,
                maskButtonLayout = CardElementLayout(left = 350, top = 280),
            ),
            // Chose which elements can be toggled by this button `CardMaskableElementEntries.all()`
            commonMaskButtonTargets = CardMaskableElementEntries.all(),
            // Use `listOf(CardMaskableElement.CVV)` to mask CVV by default
            // following details will be showed masked by default
            shouldBeMaskedDefault = CardMaskableElementEntries.all(),
            // Configure progressBar, if null - do not show
            progressBar = CardElementsItemConfig(
                detailsColor = R.color.white_80,
                detailsLayout = CardElementLayout(right = 0, bottom = 0), // paddings from center
            ),
        )
    }
}

data class CardPresenterElementConfig(
    @StringRes val labelResource: Int? = null,
    @StyleRes val titleAppearanceResId: Int? = null,
    @StyleRes val dataAppearanceResId: Int? = null,
): Serializable
data class CardPresenterConfig(
    // when (LanguageHelper().getLanguage(niInput)) {
    //    "ar" -> false
    //    else -> true
    //}
    val shouldDefaultLanguage: Boolean = true,
    val cardNumber: CardPresenterElementConfig? = null,
    val expiry: CardPresenterElementConfig? = null,
    val cvv: CardPresenterElementConfig? = null,
    val cardHolder: CardPresenterElementConfig? = null,

    val commonMaskButtonTargets: List<CardMaskableElement> = CardMaskableElementEntries.all(),
    // define initial state of masking
    val shouldBeMaskedDefault: List<CardMaskableElement> = listOf(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.EXPIRY,
        CardMaskableElement.CVV,
        CardMaskableElement.CARDHOLDER,
    ),
): Serializable
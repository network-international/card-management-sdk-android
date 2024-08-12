package ae.network.nicardmanagementsdk.api.models.input

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElementEntries
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import java.io.Serializable

data class CardElementLayout(
    val top: Int? = null,
    val bottom: Int? = null,
    val left: Int? = null,
    val right: Int? = null,
): Serializable

data class CardElementCopyButton(
    @DrawableRes var imageDefault: Int,
    @DrawableRes var imageSelected: Int? = null,
    @StringRes val contentDescription: Int? = null,
    val layout: CardElementLayout,
    val targets: List<CardMaskableElement>,
    val template: String? = null
): Serializable

data class CardElementMaskButton(
    @DrawableRes var imageDefault: Int,
    @DrawableRes var imageSelected: Int? = null,
    @StringRes val contentDescription: Int? = null,
    val layout: CardElementLayout,
    val targets: List<CardMaskableElement>,
    val template: String? = null
): Serializable

data class CardElementLabel(
    @StringRes val text: Int,
    // @ColorRes val color: Int? = null, // use null for default
    @StyleRes val appearanceResId: Int? = null, // color, font, ... use null for default
    val layout: CardElementLayout
): Serializable

data class CardElementDetails(
    // @ColorRes val color: Int? = null, // use null for default
    val layout: CardElementLayout,
    @StyleRes val appearanceResId: Int? // color, font, ... use null for default
): Serializable

data class CardProgressBarConfig(
    val paddingFromCenter: CardElementLayout? = null,
    @ColorRes val color: Int? = null, // use null for default
): Serializable

// TODO: define interface and several implementations - label / details / button and use suitable configs there
// example: ConstraintInstructions
data class CardElementsItemConfig(
    val label: CardElementLabel? = null,
    val details: CardElementDetails,
    var copyButton: CardElementCopyButton? = null,
    var maskButton: CardElementMaskButton? = null,
): Serializable
data class CardElementsConfig(
    val cardNumber: CardElementsItemConfig? = null,
    val expiry: CardElementsItemConfig? = null,
    val cvv: CardElementsItemConfig? = null,
    val cardHolder: CardElementsItemConfig? = null,
    // common mask button - toggle all elements together
    val commonMaskButton: CardElementMaskButton? = null, // common show details button
    // define initial state of masking
    val shouldBeMaskedDefault: List<CardMaskableElement> = listOf(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.EXPIRY,
        CardMaskableElement.CVV,
        CardMaskableElement.CARDHOLDER,
    ),
    // if not null - standard progressIndicator shows progress
    val progressBar: CardProgressBarConfig? = null,
): Serializable {
    companion object {
        fun default(copyTargets: List<CardMaskableElement> = listOf(CardMaskableElement.CARDNUMBER), copyTemplate: String? = null) = CardElementsConfig(
            cardNumber = CardElementsItemConfig(
                label = CardElementLabel(
                    text = R.string.card_number_en, // pass string from desired language
                    layout = CardElementLayout(left = 10, top = 47),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberLabel
                ),
                details = CardElementDetails(
                    layout = CardElementLayout(left = 10, top = 58),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberData
                ),
                copyButton = CardElementCopyButton( // use null to hide button
                    imageDefault = R.drawable.ic_baseline_content_copy,
                    layout = CardElementLayout(left = 185, top = 62),
                    targets = copyTargets,
                    template = copyTemplate,
                    contentDescription = R.string.copy_to_clipboard_image_content_description
                ),
                maskButton = null,
            ),
            expiry = CardElementsItemConfig(
                label = CardElementLabel(
                    text = R.string.card_expiry_en, // pass string from desired language
                    layout = CardElementLayout(left = 10, top = 87),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardExpiryLabel
                ),
                details = CardElementDetails(
                    layout = CardElementLayout(left = 10, top = 98),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardExpiryData
                ),
                copyButton = null,
                maskButton = null,
            ),
            cvv = CardElementsItemConfig(
                label = CardElementLabel(
                    text = R.string.card_cvv_en, // pass string from desired language
                    layout = CardElementLayout(left = 73, top = 87),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardCvvLabel
                ),
                details = CardElementDetails(
                    layout = CardElementLayout(left = 73, top = 98),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardCvvData
                ),
                copyButton = null,
                maskButton = null,
            ),
            cardHolder = CardElementsItemConfig(
                label = CardElementLabel(
                    text = R.string.card_name_en, // pass string from desired language
                    layout = CardElementLayout(left = 10, top = 131),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardHolderLabel
                ),
                details = CardElementDetails(
                    layout = CardElementLayout(left = 10, top = 142),
                    appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardHolderData
                ),
                copyButton = null,
                maskButton = null,
            ),
            commonMaskButton = CardElementMaskButton(
                imageDefault = R.drawable.ic_reveal_details,
                imageSelected = R.drawable.ic_hide_details,
                contentDescription = R.string.credentials_toggle_image_content_description,
                layout = CardElementLayout(left = 127, top = 102),
                targets = CardMaskableElementEntries.all() // Chose which elements can be toggled by this button `CardMaskableElementEntries.all()`
            ),
            // Use `listOf(CardMaskableElement.CVV)` to mask CVV by default
            // following details will be showed masked by default
            shouldBeMaskedDefault = CardMaskableElementEntries.all(),
            // Configure progressBar, if null - do not show
            progressBar = CardProgressBarConfig(
                color = R.color.white_80,
                paddingFromCenter = CardElementLayout(right = 0, bottom = 0), // paddings from center
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

/// Next classes temporary moved here, will be deleted soon
data class NIDisplayAttributes(

    // the next three parameter are optional
    // if set an custom layout will be displayed on completion, and the component will navigate
    // back on "doneButton" specified as an @IdRes val buttonResId: Int
    val setPinMessageAttributes: PinMessageAttributes? = null,
    val verifyPinMessageAttributes: PinMessageAttributes? = null,
    val changePinMessageAttributes: PinMessageAttributes? = null,

    // This parameter is optional.
    // If not set the SDK will follow your parent app day/night mode based on OS settings or as requested by your app.
    // The recommended way for using this parameter is to leave it unset, unless you have some special requirements.
    // If a value is set, the SDK will emulate (force) day/night mode, regardless of the system settings.
    val theme: NITheme? = null,

    // This parameter is optional.
    // If not set, the SDK will automatically choose English as default language.
    // If a value is set, the SDK will force English/Arabic language.
    val language: NILanguage? = null
): Serializable

enum class NITheme: Serializable {
    LIGHT, DARK_APP_COMPAT, DARK_MATERIAL
}

enum class NILanguage : Serializable {
    ENGLISH, ARABIC
}

data class UIFont(
    @FontRes
    val fontRes: Int? = null,
    // interpreted as "scaled pixel" units Sp
    val textSize: Int
): Serializable
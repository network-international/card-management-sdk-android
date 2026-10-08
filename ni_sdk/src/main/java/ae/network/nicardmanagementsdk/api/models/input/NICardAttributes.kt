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
    var contentDescription: UIElementText? = null,
    var layout: CardElementLayout,
    var targets: List<CardMaskableElement>,
    var template: String? = null
): Serializable

data class CardElementMaskButton(
    @DrawableRes var imageDefault: Int,
    @DrawableRes var imageSelected: Int? = null,
    var contentDescription: UIElementText? = null,
    var layout: CardElementLayout,
    var targets: List<CardMaskableElement>,
    var template: String? = null
): Serializable

sealed class UIElementText: Serializable {
    class Int(@StringRes val value: kotlin.Int): UIElementText()
    class String(val value: kotlin.String): UIElementText()
}

data class CardElementLabel(
    var text: UIElementText, // use either StringRes or String
    @StyleRes var appearanceResId: Int? = null, // color, font, ... use null for default
    var layout: CardElementLayout
): Serializable

data class CardElementDetails(
    var layout: CardElementLayout,
    @StyleRes var appearanceResId: Int? // color, font, ... use null for default
): Serializable

data class CardProgressBarConfig(
    var paddingFromCenter: CardElementLayout? = null,
    @ColorRes var color: Int? = null, // use null for default
): Serializable

// TODO: define interface and several implementations - label / details / button and use suitable configs there
// example: ConstraintInstructions
data class CardElementsItemConfig(
    var label: CardElementLabel? = null,
    var details: CardElementDetails,
    var copyButton: CardElementCopyButton? = null,
    var maskButton: CardElementMaskButton? = null,
): Serializable

data class CardElementsConfig(
    var cardNumber: CardElementsItemConfig? = null,
    var expiry: CardElementsItemConfig? = null,
    var cvv: CardElementsItemConfig? = null,
    var cardHolder: CardElementsItemConfig? = null,
    // common mask button - toggle all elements together
    var commonMaskButton: CardElementMaskButton? = null, // common show details button
    // define initial state of masking
    var shouldBeMaskedDefault: List<CardMaskableElement> = listOf(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.EXPIRY,
        CardMaskableElement.CVV,
        CardMaskableElement.CARDHOLDER,
    ),
    // if not null - standard progressIndicator shows progress
    var progressBar: CardProgressBarConfig? = null,
): Serializable {
    companion object {
        fun default(copyTargets: List<CardMaskableElement> = listOf(CardMaskableElement.CARDNUMBER), copyTemplate: String? = null) = CardElementsConfig(
            cardNumber = CardElementsItemConfig(
                label = CardElementLabel(
                    text = UIElementText.Int(R.string.card_number_en), // pass string from desired language
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
                    contentDescription = UIElementText.Int(R.string.copy_to_clipboard_image_content_description)
                ),
                maskButton = null,
            ),
            expiry = CardElementsItemConfig(
                label = CardElementLabel(
                    text = UIElementText.Int(R.string.card_expiry_en), // pass string from desired language
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
                    text = UIElementText.Int(R.string.card_cvv_en), // pass string from desired language
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
                    text = UIElementText.Int(R.string.card_name_en), // pass string from desired language
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
                contentDescription = UIElementText.Int(R.string.credentials_toggle_image_content_description),
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
    var text: UIElementText? = null,
    @StyleRes var titleAppearanceResId: Int? = null,
    @StyleRes var dataAppearanceResId: Int? = null,
): Serializable

data class CardPresenterConfig(
    var cardNumber: CardPresenterElementConfig?,
    var expiry: CardPresenterElementConfig?,
    var cvv: CardPresenterElementConfig?,
    var cardHolder: CardPresenterElementConfig?,
    var shouldBeMaskedDefault: List<CardMaskableElement>
): Serializable {
    companion object {
        fun default() = CardPresenterConfig(
            cardNumber = CardPresenterElementConfig(
                text = UIElementText.Int(R.string.card_number_en),
                titleAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberLabel,
                dataAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberData
            ),
            expiry = CardPresenterElementConfig(
                text = UIElementText.Int(R.string.card_expiry_en),
                titleAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardExpiryLabel,
                dataAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardExpiryData
            ),
            cvv = CardPresenterElementConfig(
                text = UIElementText.Int(R.string.card_cvv_en),
                titleAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardCvvLabel,
                dataAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardCvvData
            ),
            cardHolder = CardPresenterElementConfig(
                text = UIElementText.Int(R.string.card_name_en),
                titleAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardHolderLabel,
                dataAppearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardHolderData
            ),
            shouldBeMaskedDefault = CardMaskableElementEntries.all()
        )
    }
}

/// Next classes temporary moved here, will be deleted soon
data class NIDisplayAttributes(
    // This parameter is optional.
    // If not set the SDK will follow your parent app day/night mode based on OS settings or as requested by your app.
    // The recommended way for using this parameter is to leave it unset, unless you have some special requirements.
    // If a value is set, the SDK will emulate (force) day/night mode, regardless of the system settings.
    val theme: NITheme? = null,
): Serializable

enum class NITheme: Serializable {
    LIGHT, DARK_APP_COMPAT, DARK_MATERIAL
}
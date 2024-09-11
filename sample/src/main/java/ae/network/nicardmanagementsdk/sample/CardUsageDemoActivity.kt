package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementCopyButton
import ae.network.nicardmanagementsdk.api.models.input.CardElementDetails
import ae.network.nicardmanagementsdk.api.models.input.CardElementLabel
import ae.network.nicardmanagementsdk.api.models.input.CardElementLayout
import ae.network.nicardmanagementsdk.api.models.input.CardElementMaskButton
import ae.network.nicardmanagementsdk.api.models.input.CardElementText
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.CardElementsItemConfig
import ae.network.nicardmanagementsdk.api.models.input.CardProgressBarConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragment
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentListener
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.views.CustomBackNavigationView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ae.network.nicardmanagementsdk.sample.MainActivity.Companion.TAG
import com.example.nicardmanagementapp.R

class CardUsageDemoActivity : AppCompatActivity(), CardDetailsFragmentListener {

    private lateinit var niInput: NIInput
    private lateinit var niPinFormType: NIPinFormType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_usage_demo)
        getDataFromBundle()
        initializeUI()
    }
    override fun onStart() {
        super.onStart()
    }

    private fun getDataFromBundle() {
        intent.getSerializableExtraCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_INPUT} is missing")

        intent?.getSerializableExtraCompat<NIPinFormType>(Extra.EXTRA_NI_PIN_FORM_TYPE)?.let {
            niPinFormType = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_TYPE} is missing")
    }

    private fun initializeUI() {
        findViewById<CustomBackNavigationView>(R.id.customBackNavigationView)?.let {
            it.setOnBackButtonClickListener {
                this.finish()
            }
        }

        val cardDetailsFragment = CardDetailsFragment.newInstance(
            niInput,
            // Only show a toast for Android 12 and lower.
            copyToClipboardMessage = ae.network.nicardmanagementsdk.R.string.copied_to_clipboard_en,
            config = CardElementsConfig(
                cardNumber = CardElementsItemConfig(
                    label = CardElementLabel(
                        text = CardElementText.Int(R.string.card_number_app), // pass string from desired language
                        layout = CardElementLayout(left = 16, top = 47),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardNumberLabel
                    ),
                    details = CardElementDetails(
                        layout = CardElementLayout(right = 50, top = 41),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardNumberData
                    ),
                    copyButton = CardElementCopyButton( // use null to hide button
                        imageDefault = R.drawable.ic_copy_buttonimg_app,
                        layout = CardElementLayout(right = 16, top = 46),
                        targets = listOf(CardMaskableElement.CARDNUMBER),
                        template = null, // provide template for formatting copied data
                        contentDescription = CardElementText.Int(ae.network.nicardmanagementsdk.R.string.copy_to_clipboard_image_content_description)
                    ),
                    maskButton = CardElementMaskButton(
                        imageDefault = R.drawable.ic_eye_show_buttonimg_app,
                        imageSelected = R.drawable.ic_eye_hide_buttonimg_app,
                        layout = CardElementLayout(right = 33, top = 46),
                        targets = listOf(CardMaskableElement.CARDNUMBER),
                    ),
                ),
                cardHolder = CardElementsItemConfig(
                    label = CardElementLabel(
                        //CardElementText.Int(R.string.card_name_app), pass string from desired language
                        text = CardElementText.String("card name string"),
                        layout = CardElementLayout(left = 16, top = 18),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardHolderLabel
                    ),
                    details = CardElementDetails(
                        layout = CardElementLayout(right = 50, top = 17),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardHolderData
                    ),
                    copyButton = CardElementCopyButton( // use null to hide button
                        imageDefault = R.drawable.ic_copy_buttonimg_app,
                        layout = CardElementLayout(right = 16, top = 17),
                        targets = listOf(CardMaskableElement.CARDHOLDER),
                        template = null, // provide template for formatting copied data
                        contentDescription = CardElementText.Int(ae.network.nicardmanagementsdk.R.string.copy_to_clipboard_image_content_description)
                    ),
                    maskButton = CardElementMaskButton(
                        imageDefault = R.drawable.ic_eye_show_buttonimg_app,
                        imageSelected = R.drawable.ic_eye_hide_buttonimg_app,
                        layout = CardElementLayout(right = 33, top = 46),
                        targets = listOf(CardMaskableElement.CARDHOLDER),
                    ),
                ),
                expiry = CardElementsItemConfig(
                    label = CardElementLabel(
                        text = CardElementText.Int(R.string.card_expiry_app), // pass string from desired language
                        layout = CardElementLayout(left = 16, top = 76),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardExpiryLabel
                    ),
                    details = CardElementDetails(
                        layout = CardElementLayout(right = 16, top = 75),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardExpiryData
                    ),
                    copyButton = null,
                    maskButton = null,
                ),
                cvv = CardElementsItemConfig(
                    label = CardElementLabel(
                        text = CardElementText.Int(R.string.card_cvv_app), // pass string from desired language
                        layout = CardElementLayout(left = 16, top = 105),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardCvvLabel
                    ),
                    details = CardElementDetails(
                        layout = CardElementLayout(right = 50, top = 103),
                        appearanceResId = R.style.TextAppearance_App_CardElement_CardCvvData
                    ),
                    copyButton = CardElementCopyButton( // use null to hide button
                        imageDefault = R.drawable.ic_copy_buttonimg_app,
                        layout = CardElementLayout(right = 16, top = 103),
                        targets = listOf(CardMaskableElement.CVV),
                        template = null, // provide template for formatting copied data
                        contentDescription = CardElementText.Int(ae.network.nicardmanagementsdk.R.string.copy_to_clipboard_image_content_description)
                    ),
                    maskButton = CardElementMaskButton(
                        imageDefault = R.drawable.ic_eye_show_buttonimg_app,
                        imageSelected = R.drawable.ic_eye_hide_buttonimg_app,
                        layout = CardElementLayout(right = 33, top = 103),
                        targets = listOf(CardMaskableElement.CARDHOLDER),
                    ),
                ),
                commonMaskButton = CardElementMaskButton(
                    imageDefault = R.drawable.ic_eye_show_buttonimg_red,
                    imageSelected = R.drawable.ic_eye_hide_buttonimg_red,
                    contentDescription = CardElementText.Int(ae.network.nicardmanagementsdk.R.string.credentials_toggle_image_content_description),
                    layout = CardElementLayout(left = 80, top = 76),
                    targets = listOf( // Chose which elements can be toggled by this button `CardMaskableElementEntries.all()`
                        CardMaskableElement.CARDNUMBER,
                        CardMaskableElement.CARDHOLDER,
                        CardMaskableElement.EXPIRY,
                    ),
                ),
                // Use `listOf(CardMaskableElement.CVV)` to mask CVV by default
                // following details will be showed masked by default
                shouldBeMaskedDefault = listOf(
                    CardMaskableElement.CARDNUMBER,
                    CardMaskableElement.CARDHOLDER,
                    CardMaskableElement.EXPIRY,
                ),
                // Configure progressBar, if null - do not show
                progressBar = CardProgressBarConfig(
                    color = R.color.colorAccentApp,
                    paddingFromCenter = CardElementLayout(right = 0, bottom = 0), // paddings from center
                ),
            )
        )
        supportFragmentManager.beginTransaction().apply {
            add(R.id.card_container, cardDetailsFragment, CardDetailsFragment.TAG)
            commit()
        }
    }

    override fun onCardDetailsFragmentCompletion(response: SuccessErrorResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "CardUsageDemoActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "CardUsageDemoActivity ${it.error} ${it.errorMessage}")
        }
    }
}
package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementLayout
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.CardElementsItemConfig
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
import com.example.nicardmanagementapp.R.color.colorAccentApp
import com.example.nicardmanagementapp.R.color.white

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

        // Use your color from resources, only textViews will be affected, not buttons
        val elementsColor =  white
        val detailsColor =  colorAccentApp
        val cardDetailsFragment = CardDetailsFragment.newInstance(
            niInput,
            config = CardElementsConfig(
                cardNumber = CardElementsItemConfig(
                    labelColor = elementsColor, // use null for default
                    detailsColor = detailsColor, // use null for default
                    labelResource = R.string.card_number_app, // use null for default
                    labelLayout = CardElementLayout(left = 32, top = 130),
                    // right - copy button - right
                    detailsLayout = CardElementLayout(right = 132, top = 114),
                    copyButtonLayout = CardElementLayout(right = 32, top = 126),
                    copyButtonImage = R.drawable.ic_copy_buttonimg_app, // use null to hide button
                    // add individual mask button if needed
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_app,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_app,
                    maskButtonLayout = CardElementLayout(right = 84, top = 126),
                ),
                cardHolder = CardElementsItemConfig(
                    labelColor = elementsColor, // use null for default
                    detailsColor = detailsColor, // use null for default
                    labelResource = R.string.card_name_app, // use null for default
                    labelLayout = CardElementLayout(left = 32, top = 50),
                    detailsLayout = CardElementLayout(right = 132, top = 46),
                    copyButtonLayout = CardElementLayout(right = 32, top = 46),
                    copyButtonImage = R.drawable.ic_copy_buttonimg_app, // use null to hide button
                    // add individual mask button if needed
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_app,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_app,
                    // right 16 -- copy btn -- 8
                    maskButtonLayout = CardElementLayout(right = 84, top = 46),
                ),
                expiry = CardElementsItemConfig(
                    labelColor = elementsColor, // use null for default
                    detailsColor = detailsColor, // use null for default
                    labelResource = R.string.card_expiry_app, // use null for default
                    labelLayout = CardElementLayout(left = 32, top = 210),
                    detailsLayout = CardElementLayout(right = 32, top = 206),
                    copyButtonLayout = null,
                    copyButtonImage = null, // use null to hide button
                    // add individual mask button if needed
                    maskButtonHideImage = null,
                    maskButtonShowImage = null,
                    maskButtonLayout = null
                ),
                cvv = CardElementsItemConfig(
                    labelColor = elementsColor, // use null for default
                    detailsColor = detailsColor, // use null for default
                    labelResource = R.string.card_cvv_app,
                    labelLayout = CardElementLayout(left = 32, top = 290),
                    // right - copy button --  -- mask button - right
                    detailsLayout = CardElementLayout(right = 132, top = 284),
                    // use individual button if needed
                    copyButtonLayout = CardElementLayout(right = 32, top = 284),
                    copyButtonImage = R.drawable.ic_copy_buttonimg_app, // use null to hide button
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_app,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_app,

                    maskButtonLayout = CardElementLayout(right = 84, top = 284),
                ),
                commonMaskButton = CardElementsItemConfig(
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_red,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_red,
                    maskButtonLayout = CardElementLayout(left = 216, top = 206),
                ),
                // Chose which elements can be toggled by this button `CardMaskableElementEntries.all()`
                commonMaskButtonTargets = listOf(
                    CardMaskableElement.CARDNUMBER,
                    CardMaskableElement.CARDHOLDER,
                    CardMaskableElement.EXPIRY,
                ),
                // Use `listOf(CardMaskableElement.CVV)` to mask CVV by default
                // following details will be showed masked by default
                shouldBeMaskedDefault = listOf(
                    CardMaskableElement.CARDNUMBER,
                    CardMaskableElement.CARDHOLDER,
                    CardMaskableElement.EXPIRY,
                ),
                // Configure progressBar, if null - do not show
                progressBar = CardElementsItemConfig(
                    detailsColor = detailsColor,
                    detailsLayout = CardElementLayout(right = 0, bottom =32), // paddings from center
                ),
            ),
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
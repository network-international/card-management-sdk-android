package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementLayout
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.CardElementsItemConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentBase
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentFreeForm
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElementEntries
import ae.network.nicardmanagementsdk.presentation.views.CustomBackNavigationView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ae.network.nicardmanagementsdk.sample.MainActivity.Companion.TAG
import com.example.nicardmanagementapp.R
import com.example.nicardmanagementapp.R.color.colorAccentApp
import com.example.nicardmanagementapp.R.color.colorPrimary

class CardUsageDemoActivity : AppCompatActivity(), CardDetailsFragmentBase.OnFragmentInteractionListener {

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
        val elementsColor =  colorPrimary
        val detailsColor =  colorAccentApp
        val cardDetailsFragment = CardDetailsFragmentFreeForm.newInstance(
            niInput,
            config = CardElementsConfig(
                cardNumber = CardElementsItemConfig(
                    labelColor = elementsColor, // use null for default
                    detailsColor = detailsColor, // use null for default
                    labelResource = R.string.card_number_app, // use null for default
                    labelLayout = CardElementLayout(left = 16, top = 100),
                    // right - copy button - right
                    detailsLayout = CardElementLayout(right = 116, top = 84),
                    copyButtonLayout = CardElementLayout(right = 16, top = 96),
                    copyButtonImage = R.drawable.ic_copy_buttonimg_app, // use null to hide button
                    // add individual mask button if needed
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_app,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_app,
                    maskButtonLayout = CardElementLayout(right = 68, top = 96),
                ),
                cardHolder = CardElementsItemConfig(
                    labelColor = elementsColor, // use null for default
                    detailsColor = detailsColor, // use null for default
                    labelResource = R.string.card_name_app, // use null for default
                    labelLayout = CardElementLayout(left = 16, top = 20),
                    detailsLayout = CardElementLayout(right = 116, top = 16),
                    copyButtonLayout = CardElementLayout(right = 16, top = 16),
                    copyButtonImage = R.drawable.ic_copy_buttonimg_app, // use null to hide button
                    // add individual mask button if needed
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_app,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_app,
                    // right 16 -- copy btn -- 8
                    maskButtonLayout = CardElementLayout(right = 68, top = 16),
                ),
                expiry = CardElementsItemConfig(
                    labelColor = elementsColor, // use null for default
                    detailsColor = detailsColor, // use null for default
                    labelResource = R.string.card_expiry_app, // use null for default
                    labelLayout = CardElementLayout(left = 16, top = 180),
                    detailsLayout = CardElementLayout(right = 16, top = 176),
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
                    labelLayout = CardElementLayout(left = 16, top = 260),
                    // right - copy button --  -- mask button - right
                    detailsLayout = CardElementLayout(right = 116, top = 254),
                    // use individual button if needed
                    copyButtonLayout = CardElementLayout(right = 16, top = 254),
                    copyButtonImage = R.drawable.ic_copy_buttonimg_app, // use null to hide button
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_app,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_app,

                    maskButtonLayout = CardElementLayout(right = 68, top = 254),
                ),
                commonMaskButton = CardElementsItemConfig(
                    maskButtonHideImage = R.drawable.ic_eye_hide_buttonimg_red,
                    maskButtonShowImage = R.drawable.ic_eye_show_buttonimg_red,
                    maskButtonLayout = CardElementLayout(left = 200, top = 176),
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
                // Configure progressBar
                progressBar = CardElementsItemConfig(
                    detailsColor = detailsColor,
                    detailsLayout = CardElementLayout(right = 0, bottom = 150), // paddings from center
                ),
            ),
        )
        supportFragmentManager.beginTransaction().apply {
            add(R.id.card_container, cardDetailsFragment, CardDetailsFragmentFreeForm.TAG)
            commit()
        }

        // add MainFragment to the UI
        val mainFragment = MainFragment.newInstance(niInput, niPinFormType)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.main_fragment_container, mainFragment, MainFragment.TAG)
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
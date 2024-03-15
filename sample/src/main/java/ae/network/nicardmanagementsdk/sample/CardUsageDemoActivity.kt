package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementPositioning
import ae.network.nicardmanagementsdk.api.models.input.CardElementsPositioning
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentBase
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentFreeForm
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentFromActivity
import ae.network.nicardmanagementsdk.presentation.views.CustomBackNavigationView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ae.network.nicardmanagementsdk.sample.MainActivity.Companion.TAG
import com.example.nicardmanagementapp.R
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

        // add CardDetailsFragmentFromActivity to the UI
        // val cardDetailsFragment = CardDetailsFragmentFromActivity.newInstance(niInput)
        // Use new fragment
        val cardDetailsFragment = CardDetailsFragmentFreeForm.newInstance(
            niInput,
            elementsColor = colorPrimary,
            positioning = CardElementsPositioning(
                cardNumberLabel = CardElementPositioning(start = 16, top = 16),
                cardNumberText = CardElementPositioning(start = 200, top = 16),
                cardNumberButton = CardElementPositioning(start = 700, top = 16),
                cardHolderLabel = CardElementPositioning(start = 16, top = 196),
                cardHolderText = CardElementPositioning(start = 200, top = 196),
                cardHolderButton = CardElementPositioning(start = 700, top = 196),
                expiryLabel = CardElementPositioning(start = 16, top = 76),
                expiryText = CardElementPositioning(start = 200, top = 76),
                showDetailsButton = CardElementPositioning(start = 700, top = 76),
                cvvLabel = CardElementPositioning(start = 16, top = 136),
                cvvText = CardElementPositioning(start = 200, top = 136)
            )
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
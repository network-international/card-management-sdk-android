package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentBase
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentFromActivity
import ae.network.nicardmanagementsdk.presentation.views.CustomBackNavigationView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ae.network.nicardmanagementsdk.sample.MainActivity.Companion.TAG
import com.example.nicardmanagementapp.R

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
        val cardDetailsFragment = CardDetailsFragmentFromActivity.newInstance(niInput)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.card_container, cardDetailsFragment, CardDetailsFragmentFromActivity.TAG)
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
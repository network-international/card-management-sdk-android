package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.implementation.NICardManagementForms
import ae.network.nicardmanagementsdk.api.implementation.OnSuccessErrorCancelCompletion
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.*
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragment
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.ViewPinFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nicardmanagementapp.R
import ae.network.nicardmanagementsdk.sample.adapters.EntriesListAdapter
import com.example.nicardmanagementapp.databinding.ActivityMainBinding
import ae.network.nicardmanagementsdk.sample.models.EntriesItemModel
import ae.network.nicardmanagementsdk.sample.models.SampleAppFormEntryEnum.*

class MainActivity : AppCompatActivity(),
    SetPinFragment.OnFragmentInteractionListener,
    VerifyPinFragment.OnFragmentInteractionListener,
    ChangePinFragment.OnFragmentInteractionListener,
    ViewPinFragment.OnFragmentInteractionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val niInput: NIInput
        get() = makeInputObject()
    private val pinLength: NIPinFormType
        get() = viewModel.getPINLength()

    private val niCardManagementForms = NICardManagementForms(
        this,
        displayCardDetailsOnCompletion = getCompletionHandler()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // setupObservers
        viewModel.entriesItemsLiveData.observe(this) { itemModels ->
            itemModels?.let {
                (binding.recyclerView.adapter as EntriesListAdapter).setItems(it)
            }
        }

        initializeUI()

        if (viewModel.entriesItemModels.isEmpty()) {
            val entries = listOf(
                EntriesItemModel(BANK_CODE, getString(R.string.bank_code_txt), "****"),
                EntriesItemModel(CARD_ID, getString(R.string.card_identifier_id_txt), "1111222233334444"),
                EntriesItemModel(CARD_TYPE, getString(R.string.card_identifier_type_txt), "EXID"),
                EntriesItemModel(ROOT_URL, getString(R.string.root_url_txt), "https://api-uat.network.global/sdk/v2"),
                EntriesItemModel(TOKEN, getString(R.string.token_txt), "***"),
                EntriesItemModel(PIN_LENGTH, getString(R.string.pin_length_txt), NIPinFormType.FOUR_DIGITS.name, getString(
                    R.string.pin_length_placeholder
                ))
            )
            viewModel.setEntriesItems(entries)
        }
    }

    private fun initializeUI() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = EntriesListAdapter()
            setHasFixedSize(true)
        }

        val pinFlowResources = PinManagementResources.default(
            setPinResultAttributes = makePinResultAttributes(),
            verifyPinMessageAttributes = makePinResultAttributes(),
            changePinResultAttributes = makePinResultAttributes(),
        )
        binding.setPinButton.setOnClickListener {
            // provide padding for pin forms
            val dialog = SetPinFragment.newInstance(niInput, pinLength, pinFlowResources, padding = 100)
            dialog.show(supportFragmentManager, SetPinFragment.TAG)
        }

        binding.verifyPinButton.setOnClickListener {
            val dialog = VerifyPinFragment.newInstance(niInput, pinLength, pinFlowResources, padding = 100)
            dialog.show(supportFragmentManager, VerifyPinFragment.TAG)
        }

        binding.changePinButton.setOnClickListener {
            val dialog = ChangePinFragment.newInstance(niInput, pinLength, pinFlowResources, padding = 100)
            dialog.show(supportFragmentManager, ChangePinFragment.TAG)
        }

        binding.viewPinButton.setOnClickListener {
            niCardManagementForms.displayViewPinForm(niInput,pinLength, pinFlowResources, padding = 100 )
        }
        //======
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
                else -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
            }
        }

        binding.cardDetailsButton.setOnClickListener {
            // update text in config if needed,
            // val config = CardElementsConfig.default ...
            // config.cardNumber?.label?.text  = CardElementText.String("My card #")
            // update position if needed - attach element to bottom-left corner
            // config.cardNumber?.label?.layout = CardElementLayout(bottom = 0, left = 0)
            niCardManagementForms.displayCardDetailsForm(
                niInput,
                backgroundImage = ae.network.nicardmanagementsdk.R.drawable.bg_default_mc,
                title = ae.network.nicardmanagementsdk.R.string.card_details_title_en,
                config = CardElementsConfig.default(
                    copyTargets = listOf<CardMaskableElement>(
                        CardMaskableElement.CARDNUMBER,
                        CardMaskableElement.CARDHOLDER,
                    ),
                    copyTemplate = "Card number: %s\nName: %s"
                ),
                padding = 100
            )
        }

        binding.cardDetailsFragmentButton.setOnClickListener {
            // check CardUsageDemoActivity for card view configuration
            startActivity(Intent(this@MainActivity, CardUsageDemoActivity::class.java).apply {
                putExtra(Extra.EXTRA_NI_INPUT, niInput)
                putExtra(Extra.EXTRA_NI_PIN_FORM_TYPE, pinLength)
            })
        }

        binding.cardDetailsDialogButton.setOnClickListener {
            // check CardBottomSheetDialogFragment for card view configuration
            val dialog = CardBottomSheetDialogFragment.newInstance(niInput)
            supportFragmentManager.let { dialog.show(it, CardBottomSheetDialogFragment.TAG) }
        }
    }

    private fun getCompletionHandler(): OnSuccessErrorCancelCompletion =
        { success, error, canceled ->
            val formName = "displayCardDetailsForm"
            if (canceled) {
                Log.d(TAG, "$formName canceled by the user")
            } else {
                success?.let {
                    Log.d(TAG, "$formName ${it.message}")
                }
                error?.let {
                    Log.d(TAG, "$formName ${it.error}  ${it.errorMessage}")
                }
            }
        }

    private fun makePinResultAttributes(): PinResultAttributes {
        return PinResultAttributes(
            successScreen = PinResultScreenAttributes(
                layoutId = R.layout.activity_success,
                buttonResId = R.id.doneButton
            ),
            errorScreen = PinResultScreenAttributes(
                layoutId = R.layout.activity_error,
                buttonResId = R.id.doneButton
            )
        )
    }

    private fun makeInputObject(): NIInput {
        return NIInput(
            bankCode = viewModel.entriesItemModels.first { model -> model.id == BANK_CODE }.value,
            cardIdentifierId = viewModel.entriesItemModels.first { model -> model.id == CARD_ID }.value,
            cardIdentifierType = viewModel.entriesItemModels.first { model -> model.id == CARD_TYPE }.value,
            connectionProperties = NIConnectionProperties(
                viewModel.entriesItemModels.first { model -> model.id == ROOT_URL }.value,
                viewModel.entriesItemModels.first { model -> model.id == TOKEN }.value,
                extraNetworkHeaders = hashMapOf(
                    "extraHeader1" to "DemoExtraHttpHeaderValue",
                )
            ),
            displayAttributes = NIDisplayAttributes(
                //theme = NITheme.DARK_APP_COMPAT
            )
        )
    }

    companion object {
        const val TAG = "SDKLogMessage"
    }

    override fun onChangePinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "ChangePinFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "ChangePinFragment ${it.errorMessage}")
        }
    }

    override fun onSetPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "SetPinFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "SetPinFragment ${it.errorMessage}")
        }
    }

    override fun onVerifyPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "VerifyPinFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "VerifyPinFragment ${it.errorMessage}")
        }
    }

    override fun onViewPinFragmentCompletion(response: SuccessErrorResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "ViewPinFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "ViewPinFragment ${it.errorMessage}")
        }
    }

}
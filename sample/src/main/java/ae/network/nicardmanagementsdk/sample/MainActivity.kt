package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.implementation.NICardManagementForms
import ae.network.nicardmanagementsdk.api.implementation.OnSuccessErrorCancelCompletion
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.*
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragment
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
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
    ChangePinFragment.OnFragmentInteractionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val niInput: NIInput
        get() = makeInputObject()
    private val pinLength: NIPinFormType
        get() = viewModel.getPINLength()

    private val niCardManagementForms = NICardManagementForms(
        this,
        displayCardDetailsOnCompletion = getCompletionHandler("displayCardDetailsForm")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setArchitectureComponents()
        initializeUI()
        setViewModelData()
    }

    private fun setArchitectureComponents() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initializeUI() {
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = EntriesListAdapter()
                setHasFixedSize(true)

                this@MainActivity.viewModel.entriesItemsLiveData.observe(this@MainActivity) { itemModels ->
                    itemModels?.let {
                        (adapter as EntriesListAdapter).setItems(it)
                    }
                }
            }

            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
                    else -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
                }
            }

            cardDetailsButton.setOnClickListener {
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
                    )
                )
            }

            cardDetailsFragmentButton.setOnClickListener {
                // check CardUsageDemoActivity for card view configuration
                startActivity(Intent(this@MainActivity, CardUsageDemoActivity::class.java).apply {
                    putExtra(Extra.EXTRA_NI_INPUT, niInput)
                    putExtra(Extra.EXTRA_NI_PIN_FORM_TYPE, pinLength)
                })
            }

            cardDetailsDialogButton.setOnClickListener {
                // check CardBottomSheetDialogFragment for card view configuration
                val dialog = CardBottomSheetDialogFragment.newInstance(niInput)
                supportFragmentManager.let { dialog.show(it, CardBottomSheetDialogFragment.TAG) }
            }

            val pinFlowResources = PinManagementResources.default(
                setPinResultAttributes = makePinResultAttributes(),
                verifyPinMessageAttributes = makePinResultAttributes(),
                changePinResultAttributes = makePinResultAttributes(),
            )
            setPinButton.setOnClickListener {
                // provide padding for pin forms
                val dialog = SetPinFragment.newInstance(niInput, pinLength, pinFlowResources, padding = 100)
                dialog.show(supportFragmentManager, SetPinFragment.TAG)
            }

            verifyPinButton.setOnClickListener {
                val dialog = VerifyPinFragment.newInstance(niInput, pinLength, pinFlowResources)
                dialog.show(supportFragmentManager, VerifyPinFragment.TAG)
            }

            changePinButton.setOnClickListener {
                val dialog = ChangePinFragment.newInstance(niInput, pinLength, pinFlowResources)
                dialog.show(supportFragmentManager, ChangePinFragment.TAG)
            }
        }
    }

    private fun setViewModelData() {
        if (viewModel.entriesItemModels.isEmpty()) {
            val entries = listOf(
                EntriesItemModel(BANK_CODE, getString(R.string.bank_code_txt), "D2C"),
                EntriesItemModel(CARD_ID, getString(R.string.card_identifier_id_txt), "22344402126097490505"),
                EntriesItemModel(CARD_TYPE, getString(R.string.card_identifier_type_txt), "EXID"),
                EntriesItemModel(ROOT_URL, getString(R.string.root_url_txt), "https://api-uat.network.global/sdk/v2"),
                EntriesItemModel(TOKEN, getString(R.string.token_txt), "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICItNzBNYURtTkxYYW1OR294SGFLWjliM0V3TmdvQ1JOOW5HenlSSFZJN3ZjIn0.eyJleHAiOjE3MjcyNTMxNzUsImlhdCI6MTcyNzI1MTM3NSwianRpIjoiZDVkYzEyYzctM2JiNS00NTBjLWI2NGItNDk5MGUyYjhhZjUwIiwiaXNzIjoiaHR0cHM6Ly9pZGVudGl0eS1ub25wcm9kLm5ldHdvcmsuZ2xvYmFsL2F1dGgvcmVhbG1zL05JLU5vblByb2QiLCJzdWIiOiI0NGYxMTFlZi02MTEyLTRmY2ItYjkzYS03MDJiZjljZTIwZTQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiIyMDYxOGYxMC03MmM2LTRhM2MtYWFhNC1kMzM2MWY1MmZhOGY1NjMyMDciLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImNsaWVudElkIjoiMjA2MThmMTAtNzJjNi00YTNjLWFhYTQtZDMzNjFmNTJmYThmNTYzMjA3Iiwib3JnX2lkIjoiRDJDIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LTIwNjE4ZjEwLTcyYzYtNGEzYy1hYWE0LWQzMzYxZjUyZmE4ZjU2MzIwNyJ9.Lo38nkElA1ojIdOO5VjuP8R2rdwRDssO3E0H2YcrG-_0qSxrJxyfL8io3BLue6EsnZPZnAmz4wWIW6qB2fx5dlGqnyFRSIKEgEmOYYqxLSKFr8K6TznA40bABWScKIMECXZ4ANNdvCznp9KouWk6RVlYCKapAZOFYy52G9MhdzL6RjkhbrZZCcTnyNyOCQitf_O78qa-8PKj_y9gxYrKNSyqQZBnEvJc6rafaBkZjsX2qSyGunczNOQroljyy2I2liOu7bbasCPEGf9s-XKC3Kyy8AqRhc1midmDmffd0htvl2Uunwsq3uZoROVQi-qOfXGqnv3cEDBtm3xq---AwA"),
                EntriesItemModel(PIN_LENGTH, getString(R.string.pin_length_txt), NIPinFormType.FOUR_DIGITS.name, getString(
                    R.string.pin_length_placeholder
                ))
            )
            viewModel.setEntriesItems(entries)
        }
    }

    private fun getCompletionHandler(formName: String): OnSuccessErrorCancelCompletion =
        { success, error, canceled ->
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
                    "Content-Type" to "will be ignored for existing header" // this will be ignored
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

}
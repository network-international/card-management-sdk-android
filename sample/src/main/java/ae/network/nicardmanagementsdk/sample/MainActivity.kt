package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.implementation.NICardManagement
import ae.network.nicardmanagementsdk.api.implementation.NICardManagementForms
import ae.network.nicardmanagementsdk.api.implementation.OnSuccessErrorCancelCompletion
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.*
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardMaskableElement
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragment
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragmentFromActivity
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragmentFromActivity
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragmentFromActivity
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

            enToggleButton.setOnClickListener {
                arToggleButton.isChecked = false
                noneToggleButton.isChecked = false
            }

            arToggleButton.setOnClickListener {
                enToggleButton.isChecked = false
                noneToggleButton.isChecked = false
            }

            noneToggleButton.setOnClickListener {
                enToggleButton.isChecked = false
                arToggleButton.isChecked = false
            }

            cardDetailsButton.setOnClickListener {
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
                startActivity(Intent(this@MainActivity, CardUsageDemoActivity::class.java).apply {
                    putExtra(Extra.EXTRA_NI_INPUT, niInput)
                    putExtra(Extra.EXTRA_NI_PIN_FORM_TYPE, pinLength)
                })
            }

            cardDetailsDialogButton.setOnClickListener {
                val dialog = CardBottomSheetDialogFragment.newInstance(
                    niInput,
                    config = CardElementsConfig.default(
                        copyTargets = listOf<CardMaskableElement>(
                            CardMaskableElement.CARDNUMBER,
                            CardMaskableElement.CARDHOLDER,
                        ),
                        copyTemplate = "Card number: %s\nName: %s"
                    )
                )
                supportFragmentManager.let { dialog.show(it, CardBottomSheetDialogFragment.TAG) }
            }

            setPinButton.setOnClickListener {
                val dialog = SetPinFragmentFromActivity.newInstance(niInput, pinLength, padding = 100)
                dialog.show(supportFragmentManager, SetPinFragmentFromActivity.TAG)
            }

            verifyPinButton.setOnClickListener {
                val dialog = VerifyPinFragmentFromActivity.newInstance(niInput, pinLength)
                dialog.show(supportFragmentManager, VerifyPinFragmentFromActivity.TAG)
            }

            changePinButton.setOnClickListener {
                val dialog = ChangePinFragmentFromActivity.newInstance(niInput, pinLength)
                dialog.show(supportFragmentManager, ChangePinFragmentFromActivity.TAG)
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
                EntriesItemModel(TOKEN, getString(R.string.token_txt), "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICItNzBNYURtTkxYYW1OR294SGFLWjliM0V3TmdvQ1JOOW5HenlSSFZJN3ZjIn0.eyJleHAiOjE3MjYwMzgyMjUsImlhdCI6MTcyNjAzNjQyNSwianRpIjoiMDc2NGI4ZWYtYjJlZS00MmJkLWE5Y2YtZWIzMmQ4YmMxZTIwIiwiaXNzIjoiaHR0cHM6Ly9pZGVudGl0eS1ub25wcm9kLm5ldHdvcmsuZ2xvYmFsL2F1dGgvcmVhbG1zL05JLU5vblByb2QiLCJzdWIiOiI0NGYxMTFlZi02MTEyLTRmY2ItYjkzYS03MDJiZjljZTIwZTQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiIyMDYxOGYxMC03MmM2LTRhM2MtYWFhNC1kMzM2MWY1MmZhOGY1NjMyMDciLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImNsaWVudElkIjoiMjA2MThmMTAtNzJjNi00YTNjLWFhYTQtZDMzNjFmNTJmYThmNTYzMjA3Iiwib3JnX2lkIjoiRDJDIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LTIwNjE4ZjEwLTcyYzYtNGEzYy1hYWE0LWQzMzYxZjUyZmE4ZjU2MzIwNyJ9.FdDWRwATmUNsLh_XHuAh-tLlnknkReMW66WuoyJ_GVvJC9XSSN7PDAKCppwToZ432w0M5cP2WPnx8VAAnUU7DKNIitTEBVzHJ5oeoNFrlc94gDbYaAzZPk76UJtrzmHnImUH5hfYAobxVi8ArSFigCcR0lHlCFhg-jErqxwdK0pLDe9AtzudmV2zYvd251mxD1QkeusePrGXfxvvX-WvaSqlllpwDNH_deV6QzCEnrX39BdGa3Q0Zp6HsTxfC9hjwZbFZr8fVLlUnDNHDLPv1u3K5FLB-88F9FdbzBS4O0QZWtuqVxZoymLwgKaJkUiIy4cXnnCUsIXrXdp81E_iLA"),
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

    private fun makeInputObject(): NIInput {
        return NIInput(
            bankCode = viewModel.entriesItemModels.first { model -> model.id == BANK_CODE }.value,
            cardIdentifierId = viewModel.entriesItemModels.first { model -> model.id == CARD_ID }.value,
            cardIdentifierType = viewModel.entriesItemModels.first { model -> model.id == CARD_TYPE }.value,
            connectionProperties = NIConnectionProperties(
                viewModel.entriesItemModels.first { model -> model.id == ROOT_URL }.value,
                viewModel.entriesItemModels.first { model -> model.id == TOKEN }.value,
            ),
            displayAttributes = NIDisplayAttributes(
                setPinMessageAttributes = PinMessageAttributes(
                    successAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_success,
                        buttonResId = R.id.doneButton
                    ),
                    errorAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_error,
                        buttonResId = R.id.doneButton
                    )
                ),
                verifyPinMessageAttributes = PinMessageAttributes(
                    successAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_success,
                        buttonResId = R.id.doneButton
                    ),
                    errorAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_error,
                        buttonResId = R.id.doneButton
                    )
                ),
                changePinMessageAttributes = PinMessageAttributes(
                    successAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_success,
                        buttonResId = R.id.doneButton
                    ),
                    errorAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_error,
                        buttonResId = R.id.doneButton
                    )
                ),
                //theme = NITheme.DARK_APP_COMPAT
            )
        )
    }

    companion object {
        const val TAG = "SDKLogMessage"
    }

    override fun onChangePinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "ChangePinFragmentFromActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "ChangePinFragmentFromActivity ${it.errorMessage}")
        }
    }

    override fun onSetPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "SetPinFragmentFromActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "SetPinFragmentFromActivity ${it.errorMessage}")
        }
    }

    override fun onVerifyPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "VerifyPinFragmentFromActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "VerifyPinFragmentFromActivity ${it.errorMessage}")
        }
    }

}
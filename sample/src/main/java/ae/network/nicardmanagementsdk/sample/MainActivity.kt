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
                val dialog = SetPinFragmentFromActivity.newInstance(niInput, pinLength)
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
                EntriesItemModel(BANK_CODE, getString(R.string.bank_code_txt), "AXIS4"),
                EntriesItemModel(CARD_ID, getString(R.string.card_identifier_id_txt), "12259219\$\$\$8514AB521454A85D2D810EC5F4A63286BBB0E4E57357B4947F6E6E8646FDBCFD"),
                EntriesItemModel(CARD_TYPE, getString(R.string.card_identifier_type_txt), "EXID"),
                EntriesItemModel(ROOT_URL, getString(R.string.root_url_txt), "https://api-uat.egy.network.global/sdk/v2"),
                EntriesItemModel(TOKEN, getString(R.string.token_txt), "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICItNzBNYURtTkxYYW1OR294SGFLWjliM0V3TmdvQ1JOOW5HenlSSFZJN3ZjIn0.eyJleHAiOjE3MjU2MDkzMTAsImlhdCI6MTcyNTYwNzUxMCwianRpIjoiZjM5YjVkMjQtZDI1Yi00M2E0LThlMjQtZDg4MjQ4YTFkZmQwIiwiaXNzIjoiaHR0cHM6Ly9pZGVudGl0eS1ub25wcm9kLm5ldHdvcmsuZ2xvYmFsL2F1dGgvcmVhbG1zL05JLU5vblByb2QiLCJzdWIiOiIzNmFhNWJjMS1mYzU2LTRiMGMtODFkYi00YTE4ZTM2ZmU1MjEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiI2YThlMDRjMi1hNWQ3LTQ1ZGItOTA4My0wYWE0MDNkNGJmY2YyMDc1OCIsInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SWQiOiI2YThlMDRjMi1hNWQ3LTQ1ZGItOTA4My0wYWE0MDNkNGJmY2YyMDc1OCIsIm9yZ19pZCI6IkFYSVM0IiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LTZhOGUwNGMyLWE1ZDctNDVkYi05MDgzLTBhYTQwM2Q0YmZjZjIwNzU4In0.hqHG6IObjFjcW5RZXKSAfOWhN_FcM0g_TUwpNEaoOOANQDL6awTD9oTLZe6fqNNi8G-68c-qPkj2u97DPkH69PJANfHoson4v1OQHCBcez9lHYG_6KkacQeq47zwSfKXyV-l8x68RhtDVqnPUkiZKMaIzXC5_KAJ879O3M6QzKc6w3YnFqFAg3ICdmRBs1PCRyunqBH-kzOPlubbtX33TYrDGivXaWZ7jOqYGbIpVoihFqq1ACbehyjCSzRf-rATBZjdn0VmeB9OU-eFLEq_O5ZBIvLwcBWm3zXgo-tG0EpZ9i-aCqMZKUTNrvHiit6uEq4EJCgN-cgh1feQtVQAig"),
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
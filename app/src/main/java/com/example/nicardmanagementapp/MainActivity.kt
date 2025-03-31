package com.example.nicardmanagementapp

import ae.network.nicardmanagementsdk.api.implementation.NICardManagementForms
import ae.network.nicardmanagementsdk.api.implementation.OnSuccessErrorCancelCompletion
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.*
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragment
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragmentFromActivity
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragmentFromActivity
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragmentFromActivity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nicardmanagementapp.adapters.EntriesListAdapter
import com.example.nicardmanagementapp.databinding.ActivityMainBinding
import com.example.nicardmanagementapp.models.EntriesItemModel
import com.example.nicardmanagementapp.models.SampleAppFormEntryEnum.*
import java.util.*

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
    private val paddingTop: Int
        get() = 200
    private val showHideImageResource: Int
        get() = setImage()

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
                Toast.makeText(applicationContext, "EN", Toast.LENGTH_SHORT).show()
            }

            arToggleButton.setOnClickListener {
                enToggleButton.isChecked = false
                noneToggleButton.isChecked = false
                Toast.makeText(applicationContext, "AR", Toast.LENGTH_SHORT).show()
            }

            noneToggleButton.setOnClickListener {
                enToggleButton.isChecked = false
                arToggleButton.isChecked = false
            }

            cardDetailsButton.setOnClickListener {
                niCardManagementForms.displayCardDetailsForm(niInput)
            }

            cardDetailsFragmentButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, CardUsageDemoActivity::class.java).apply {
                    putExtra(Extra.EXTRA_NI_INPUT, niInput)
                    putExtra(Extra.EXTRA_NI_PIN_FORM_TYPE, pinLength)
                })
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

//            viewPinButton.setOnClickListener {
//                startActivity(Intent(this@MainActivity, BlankActivity::class.java).apply {
//                    putExtra(Extra.EXTRA_NI_INPUT, niInput)
//                    putExtra(Extra.EXTRA_NI_PIN_FORM_TYPE, NIPinFormType.FOUR_DIGITS)
//                })
//            }

//            viewPinButton.setOnClickListener{
//                niCardManagementForms.displayViewPinForm(niInput, pinLength)
//            }
        }
    }

    private fun setViewModelData() {
        if (viewModel.entriesItemModels.isEmpty()) {
            val entries = listOf(
                EntriesItemModel(BANK_CODE, getString(R.string.bank_code_txt), "EAND"),
                EntriesItemModel(CARD_ID, getString(R.string.card_identifier_id_txt), "52913582150735206039"),
                EntriesItemModel(CARD_TYPE, getString(R.string.card_identifier_type_txt), "EXID"),
                EntriesItemModel(ROOT_URL, getString(R.string.root_url_txt), "https://apitest.network.ae"),
//                EntriesItemModel(ROOT_URL, getString(R.string.root_url_txt), "https://uat-swypapp.etisalat.ae/DigitalApp/redirection/mwallet-rest/api"),
                EntriesItemModel(TOKEN, getString(R.string.token_txt), "dhcegdk2u6hmnprzqp5yrvv8"),
                EntriesItemModel(PIN_LENGTH, getString(R.string.pin_length_txt), NIPinFormType.FOUR_DIGITS.name, getString(R.string.pin_length_placeholder))
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
//                fonts = listOf(
//                    NIFontLabelPair(
//                        UIFont(
//                            R.font.architects_daughter,
//                            18
//                        ),
//                        NILabels.CARD_HOLDER_NAME_LABEL
//                    ),
//                    NIFontLabelPair(
//                        UIFont(
//                            R.font.architects_daughter,
//                            16
//                        ),
//                        NILabels.CARD_NUMBER_VALUE_LABEL
//                    )
//                ),
                cardAttributes = NICardAttributes(
                    shouldHide = true,
                    backgroundImage = R.drawable.default_upi,
//                    textPositioning = TextPositioning(
//                        leftAlignment = 0.08f,
//                        cardNumberGroupTopAlignment = 0.95f,
//                        dateCvvGroupTopAlignment = 0.7f,
//                        cardHolderNameGroupTopAlignment = 0.5f
//                    )
                ),
//                setPinMessageAttributes = PinMessageAttributes(
//                    successAttributes = SuccessErrorScreenAttributes(
//                        layoutId = R.layout.activity_success,
//                        buttonResId = R.id.doneButton
//                    ),
//                    errorAttributes = SuccessErrorScreenAttributes(
//                        layoutId = R.layout.activity_error,
//                        buttonResId = R.id.doneButton
//                    )
//                ),
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
//                theme = NITheme.DARK
               language = NILanguage.ARABIC
            )
        )
    }

    private fun setPaddingTop() = 300

    private fun setImage() = R.drawable.default_upi

    companion object {
        const val TAG = "SDKLogMessage"
    }

    override fun onSetPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "SetPinFragmentFromActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "SetPinFragmentFromActivity ${it.errorMessage}")
        }

        response.isCancelled?.let {
            Log.d(TAG, "SetPinFragmentFromActivity ${it.cancelMessage}")
        }
    }

    override fun onChangePinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "ChangePinFragmentFromActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "ChangePinFragmentFromActivity ${it.errorMessage}")
        }

        response.isCancelled?.let {
            Log.d(TAG, "ChangePinFragmentFromActivity ${it.cancelMessage}")
        }
    }

    override fun onVerifyPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "VerifyPinFragmentFromActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "VerifyPinFragmentFromActivity ${it.errorMessage}")
        }

        response.isCancelled?.let {
            Log.d(TAG, "VerifyPinFragmentFromActivity ${it.cancelMessage}")
        }
    }

    private fun setLanguage(language: String) {
        val res: Resources = resources
        val metrics = res.displayMetrics
        val config = res.configuration
        config.setLocale(Locale(language))
        res.updateConfiguration(config, metrics)
        onConfigurationChanged(config)
    }

}
package ae.network.nicardmanagementsdk.api.implementation

import ae.network.nicardmanagementsdk.api.interfaces.NICardManagementFormsAPI
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.api.models.output.NIErrorResponse
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.CardDetailsActivity
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.activity.ViewPinActivity
import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

typealias OnSuccessErrorCancelCompletion = (isSuccess: NISuccessResponse?, isError: NIErrorResponse?, isUserCanceled: Boolean) -> Unit

class NICardManagementForms(
    private val activity: ComponentActivity,
    displayCardDetailsOnCompletion: OnSuccessErrorCancelCompletion = { _, _, _ -> }
) : NICardManagementFormsAPI {

    private var displayCardResultLauncher: ActivityResultLauncher<Intent>? = null

    init {

        displayCardResultLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            onGetResultsFromActivity(activityResult, displayCardDetailsOnCompletion)
        }
    }

    override fun displayCardDetailsForm(
        input: NIInput
    ) {
        displayCardResultLauncher?.launch(Intent(activity, CardDetailsActivity::class.java).apply {
            putExtra(Extra.EXTRA_NI_INPUT, input)
        })
    }

    override fun displayViewPinForm(input: NIInput, pinFormType: NIPinFormType) {
        displayCardResultLauncher?.launch(Intent(activity, ViewPinActivity::class.java).apply {
            putExtra(Extra.EXTRA_NI_INPUT, input)
            putExtra(Extra.EXTRA_NI_PIN_FORM_TYPE, pinFormType)
        })
    }

    private fun onGetResultsFromActivity(
        result: ActivityResult,
        onCompletion: OnSuccessErrorCancelCompletion
    ) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                onCompletion(
                    result.data?.getSerializableExtraCompat(
                        Extra.EXTRA_NI_SUCCESS_RESPONSE
                    ),
                    result.data?.getSerializableExtraCompat(
                        Extra.EXTRA_NI_ERROR_RESPONSE
                    ),
                    false
                )
            }
            Activity.RESULT_CANCELED -> {
                onCompletion(null, null, true)
            }
        }
    }
}
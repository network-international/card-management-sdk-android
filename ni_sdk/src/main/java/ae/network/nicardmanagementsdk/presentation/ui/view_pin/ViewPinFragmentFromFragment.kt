package ae.network.nicardmanagementsdk.presentation.ui.view_pin

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.Context
import android.os.Bundle

class ViewPinFragmentFromFragment : ViewPinFragment() {

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType) = ViewPinFragmentFromFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_NI_PIN_FORM_TYPE, type)
            }
        }

        const val TAG = "ViewPinFragmentFromFragment"
    }

    override fun checkSubscriber(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("${context::class.java.simpleName} must implement ViewPinFragment.OnFragmentInteractionListener")
        }
    }
}
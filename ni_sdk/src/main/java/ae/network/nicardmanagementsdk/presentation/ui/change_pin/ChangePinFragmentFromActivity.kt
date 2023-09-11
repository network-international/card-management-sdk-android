package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import android.content.Context

class ChangePinFragmentFromActivity: ChangePinFragment() {

    override fun checkSubscriber(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("${context::class.java.simpleName} must implement ChangePinFragment.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType, padding: Int = 0) = ChangePinFragmentFromActivity().apply {
            arguments = createPinWithPaddingBundle(input, type, padding)
        }

        const val TAG = "ChangePinFragmentFromActivity"
    }
}
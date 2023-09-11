package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import android.content.Context

class SetPinFragmentFromActivity: SetPinFragment() {

    override fun checkSubscriber(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("${context::class.java.simpleName} must implement SetPinFragment.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType, padding: Int = 0) = SetPinFragmentFromActivity().apply {
            arguments = createPinWithPaddingBundle(input, type, padding)
        }

        const val TAG = "SetPinFragmentFromActivity"
    }
}
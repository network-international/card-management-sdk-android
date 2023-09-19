package ae.network.nicardmanagementsdk.presentation.ui.verify_pin

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import android.content.Context

class VerifyPinFragmentFromFragment: VerifyPinFragment() {

    override fun checkSubscriber(context: Context) {
        if (parentFragment is OnFragmentInteractionListener) {
            listener = parentFragment as OnFragmentInteractionListener
        } else {
            throw RuntimeException("$parentFragment must implement VerifyPinFragment.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType, padding: Int = 0) = VerifyPinFragmentFromFragment().apply {
            arguments = createPinWithPaddingBundle(input, type, padding)
        }

        const val TAG = "VerifyPinFragmentFromFragment"
    }
}
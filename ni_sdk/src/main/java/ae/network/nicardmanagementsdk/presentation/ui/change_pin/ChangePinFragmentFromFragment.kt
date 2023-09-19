package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import android.content.Context

class ChangePinFragmentFromFragment: ChangePinFragment() {

    override fun checkSubscriber(context: Context) {
        if (parentFragment is OnFragmentInteractionListener) {
            listener = parentFragment as OnFragmentInteractionListener
        } else {
            throw RuntimeException("$parentFragment must implement ChangePinFragment.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType, padding: Int = 0) = ChangePinFragmentFromFragment().apply {
            arguments = createPinWithPaddingBundle(input, type, padding)
        }

        const val TAG = "ChangePinFragmentFromFragment"
    }
}
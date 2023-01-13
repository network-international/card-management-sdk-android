package ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.Context
import android.os.Bundle

class CardDetailsFragmentFromFragment: CardDetailsFragmentBase() {

    override fun checkSubscriber(context: Context) {
        if (parentFragment is OnFragmentInteractionListener) {
            listener = parentFragment as OnFragmentInteractionListener
        } else {
            throw RuntimeException("$parentFragment must implement CardDetailsFragmentBase.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput) = CardDetailsFragmentFromFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
            }
        }

        const val TAG = "CardDetailsFragmentFromFragment"
    }
}
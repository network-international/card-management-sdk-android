package ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.Context
import android.os.Bundle
import androidx.annotation.ColorRes

class CardDetailsFragmentFromActivity: CardDetailsFragmentBase() {

    override fun checkSubscriber(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("${context::class.java.simpleName} must implement CardDetailsFragmentBase.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, @ColorRes elementsColor: Int? = null) = CardDetailsFragmentFromActivity().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_NI_CARD_ELEMENTS_COLOR, elementsColor)
            }
        }

        const val TAG = "CardDetailsFragmentFromActivity"
    }
}
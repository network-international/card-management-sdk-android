package ae.network.nicardmanagementsdk.presentation.ui.view_pin

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.Context
import android.os.Bundle

class ViewPinFragmentFromActivity: ViewPinFragment() {

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, timer: Long = 6000L, color: String = BLACK) = ViewPinFragmentFromActivity().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_VIEW_PIN_START_TIME, timer)
                putSerializable(Extra.EXTRA_VIEW_PIN_STROKE_COLOR, color)
            }
        }
        const val TAG = "ViewPinFragmentFromActivity"
        private const val BLACK = "#FF000000"
    }

    override fun checkSubscriber(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("${context::class.java.simpleName} must implement ViewPinFragment.OnFragmentInteractionListener")
        }
    }
}
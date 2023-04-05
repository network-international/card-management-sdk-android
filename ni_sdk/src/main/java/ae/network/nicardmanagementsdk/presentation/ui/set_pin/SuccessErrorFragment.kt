package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlin.properties.Delegates

class SuccessErrorFragment : Fragment() {

    private var layoutId by Delegates.notNull<Int>()
    private var buttonId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(LAYOUT_ID)?.let {
            layoutId = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable $LAYOUT_ID is missing")

        arguments?.getInt(BUTTON_ID)?.let {
            buttonId = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable $BUTTON_ID is missing")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(buttonId)?.setOnClickListener {
            (parentFragment as DialogFragment).dismiss()
        }
    }

    companion object {
        const val TAG = "SuccessErrorFragment"
        @JvmStatic fun newInstance(@LayoutRes layoutId: Int, @IdRes buttonId: Int) =
                SuccessErrorFragment().apply {
                    arguments = Bundle().apply {
                        putInt(LAYOUT_ID, layoutId)
                        putInt(BUTTON_ID, buttonId)
                    }
                }

        private const val LAYOUT_ID = "layout_id"
        private const val BUTTON_ID = "button_id"
    }
}
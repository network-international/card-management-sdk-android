package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.interfaces.asSuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.di.Injector
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetPinFragment : SetPinDialogFragmentBase<SetPinViewModel>() {

    override lateinit var viewModel: SetPinViewModel
    protected var listener: OnFragmentInteractionListener? = null

    override fun checkSubscriber(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else if (parentFragment is OnFragmentInteractionListener) {
            listener = parentFragment as OnFragmentInteractionListener
        } else {
            throw RuntimeException("Must implement SetPinFragment.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType, padding: Int = 0) = SetPinFragment().apply {
            arguments = createPinWithPaddingBundle(input, type, padding)
        }

        const val TAG = "SetPinFragment"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setArchitectureComponents(niInput)
        initializeUI()
        super.setViewModelData()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setArchitectureComponents(niInput : NIInput) {
        val navTitleText = UIElementText.Int(R.string.set_pin_title_en)
        val screenTitleText = UIElementText.Int(R.string.set_pin_description_enter_pin_en)
        val secondStepTitleText = UIElementText.Int(R.string.set_pin_description_re_enter_pin_en)
        val notMatchTitleText = UIElementText.Int(R.string.set_pin_description_pin_not_match_en)

        val factory = Injector.getInstance(requireContext()).provideSetPinViewModelFactory(
            niInput,
            navTitleText,
            screenTitleText,
            secondStepTitleText,
            notMatchTitleText
        )
        viewModel = ViewModelProvider(this, factory)[SetPinViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.context = requireContext()
    }

    override fun initializeUI() {
        super.initializeUI()

        viewModel.onResultSingleLiveEvent.observe(this) { successErrorResponse ->
            successErrorResponse?.let { response ->
                lifecycleScope.launch {
                    delay(500)
                    this@SetPinFragment.successErrorCancelResponse = response.asSuccessErrorCancelResponse()
                    niInput.displayAttributes?.setPinMessageAttributes?.let {
                        showSuccessErrorFragment(it,response.isSuccess != null)
                    } ?: dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onSetPinFragmentCompletion(successErrorCancelResponse)
    }

    interface OnFragmentInteractionListener {
        fun onSetPinFragmentCompletion(response: SuccessErrorCancelResponse)
    }
}
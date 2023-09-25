package ae.network.nicardmanagementsdk.presentation.ui.verify_pin

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.interfaces.asSuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinDialogFragmentBase
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class VerifyPinFragment : SetPinDialogFragmentBase<VerifyPinViewModel>() {

    override lateinit var viewModel: VerifyPinViewModel
    protected var listener: OnFragmentInteractionListener? = null

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
        val factory = Injector.getInstance(requireContext()).provideVerifyPinViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[VerifyPinViewModel::class.java]
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
                    this@VerifyPinFragment.successErrorCancelResponse = response.asSuccessErrorCancelResponse()
                    niInput.displayAttributes?.verifyPinMessageAttributes?.let {
                        showSuccessErrorFragment(it,response.isSuccess != null)
                    } ?: dismiss()
                }
            }
        }

        viewModel.updateNIInput(niInput)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onVerifyPinFragmentCompletion(successErrorCancelResponse)
    }

    interface OnFragmentInteractionListener {
        fun onVerifyPinFragmentCompletion(response: SuccessErrorCancelResponse)
    }
}
package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.interfaces.asSuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.api.models.input.PinManagementResources
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinDialogFragmentBase
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePinFragment : SetPinDialogFragmentBase<ChangePinViewModel>() {

    override lateinit var viewModel: ChangePinViewModel
    protected var listener: OnFragmentInteractionListener? = null

    override fun checkSubscriber(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else if (parentFragment is OnFragmentInteractionListener) {
            listener = parentFragment as OnFragmentInteractionListener
        } else {
            throw RuntimeException("Must implement ChangePinFragment.OnFragmentInteractionListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType, texts: PinManagementResources, padding: Int = 0) = ChangePinFragment().apply {
            arguments = createPinWithPaddingBundle(input, type, texts, padding)
        }

        const val TAG = "ChangePinFragment"
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
        val navTitleText = texts.changePin.navTitleText
        val screenTitleText = texts.changePin.screenTitleText
        val newPinTitleText = texts.changePin.newPinTitleText
        val approvePinTitleText = texts.changePin.approvePinTitleText
        val notMatchTitleText = texts.changePin.notMatchTitleText
        val factory = Injector.getInstance(requireContext()).provideChangePinViewModelFactory(niInput, navTitleText, screenTitleText, newPinTitleText, approvePinTitleText, notMatchTitleText)
        viewModel = ViewModelProvider(this, factory)[ChangePinViewModel::class.java]
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
                    this@ChangePinFragment.successErrorCancelResponse = response.asSuccessErrorCancelResponse()
                    texts.changePin.resultAttributes?.let {
                        showSuccessErrorFragment(it,response.isSuccess != null)
                    } ?: dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onChangePinFragmentCompletion(successErrorCancelResponse)
    }

    interface OnFragmentInteractionListener {
        fun onChangePinFragmentCompletion(response: SuccessErrorCancelResponse)
    }
}
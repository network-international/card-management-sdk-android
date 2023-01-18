package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinDialogFragmentBase
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class ChangePinFragment : SetPinDialogFragmentBase<ChangePinViewModel>() {

    override lateinit var viewModel: ChangePinViewModel
    protected var listener: OnFragmentInteractionListener? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setArchitectureComponents(niInput)
        initializeUI()
        super.setViewModelData()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setArchitectureComponents(niInput : NIInput) {
        val factory = Injector.getInstance(requireContext()).provideChangePinViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[ChangePinViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.context = requireContext()
    }

    override fun initializeUI() {
        super.initializeUI()
        viewModel.onResultSingleLiveEvent.observe(this) { successErrorResponse ->
            successErrorResponse?.let {
                lifecycleScope.launch {
                    delay(500)
                    dismiss()
                    listener?.onChangePinFragmentCompletion(it)
                }
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onChangePinFragmentCompletion(response: SuccessErrorResponse)
    }
}
package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.di.Injector
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class SetPinFragment : SetPinDialogFragmentBase<SetPinViewModel>() {

    override lateinit var viewModel: SetPinViewModel
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
        val factory = Injector.getInstance(requireContext()).provideSetPinViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[SetPinViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.context = requireContext()
    }

    override fun initializeUI() {
        super.initializeUI()
        viewModel.onResultSingleLiveEvent.observe(this) { successErrorResponse ->
            successErrorResponse?.let {
                lifecycleScope.launch {
                    listener?.onSetPinFragmentCompletion(it)
                    delay(500)
                    dismiss()
                }
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onSetPinFragmentCompletion(response: SuccessErrorResponse)
    }
}
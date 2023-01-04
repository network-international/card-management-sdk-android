package ae.network.nicardmanagementsdk.presentation.ui.change_pin

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinActivityBase
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePinActivity : SetPinActivityBase<ChangePinViewModel>() {

    override lateinit var viewModel: ChangePinViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setArchitectureComponents(niInput)
        initializeUI()
        super.setViewModelData()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setArchitectureComponents(niInput : NIInput) {
        val factory = Injector.getInstance(this).provideChangePinViewModelFactory(niInput)
        viewModel = ViewModelProvider(this, factory)[ChangePinViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.context = this
    }

    override fun initializeUI() {
        super.initializeUI()
        viewModel.onResultSingleLiveEvent.observe(this) { successErrorResponse ->
            successErrorResponse?.let {
                it.isSuccess?.let {
                    lifecycleScope.launch {
                        setResult(
                            Activity.RESULT_OK,
                            Intent().apply {
                                putExtra(Extra.EXTRA_NI_SUCCESS_RESPONSE, it)
                            }
                        )
                        delay(500)
                        navigateBack()
                    }
                }
                it.isError?.let {
                    lifecycleScope.launch {
                        setResult(
                            Activity.RESULT_OK,
                            Intent().apply {
                                putExtra(Extra.EXTRA_NI_ERROR_RESPONSE, it)
                            }
                        )
                        viewModel.resetChangePinState()
                        delay(500)
                        navigateBack()
                    }
                }
            }
        }
    }
}
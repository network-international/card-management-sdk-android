package ae.network.nicardmanagementsdk.presentation.ui.view_pin.activity

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.databinding.ActivityViewPinBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseActivityWithPinForm
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.ViewPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.ViewPinFragmentFromActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewPinActivity : BaseActivityWithPinForm<ViewPinViewModel>(), ViewPinFragment.OnFragmentInteractionListener {

    override lateinit var viewModel: ViewPinViewModel
    private lateinit var binding: ActivityViewPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setArchitectureComponents()
        initializeUI()
    }

    private fun setArchitectureComponents() {
        val factory = Injector.getInstance(this).provideViewPinViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[ViewPinViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_pin)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initializeUI() {
        binding.customBackNavigationView.setOnBackButtonClickListener {
            finish()
        }
        val viewPinFragment = ViewPinFragmentFromActivity.newInstance(niInput)
        supportFragmentManager.beginTransaction().apply {
            add(binding.viewPinContainer.id, viewPinFragment, ViewPinFragmentFromActivity.TAG)
            commit()
        }
    }

    override fun onViewPinFragmentCompletion(response: SuccessErrorResponse) {
        response.isSuccess?.let {
            setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    putExtra(Extra.EXTRA_NI_SUCCESS_RESPONSE, it)
                }
            )
        }
        response.isError?.let {
            lifecycleScope.launch {
                setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        putExtra(Extra.EXTRA_NI_ERROR_RESPONSE, it)
                    }
                )
                delay(500)
                navigateBack()
            }
        }
    }
}
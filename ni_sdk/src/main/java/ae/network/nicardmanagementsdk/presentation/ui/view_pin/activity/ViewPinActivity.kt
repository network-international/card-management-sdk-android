package ae.network.nicardmanagementsdk.presentation.ui.view_pin.activity

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.api.models.input.PinManagementResources
import ae.network.nicardmanagementsdk.databinding.ActivityViewPinBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.helpers.ThemeHelper
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.ViewPinFragment
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewPinActivity : AppCompatActivity(), ViewPinFragment.OnFragmentInteractionListener {

    lateinit var viewModel: ViewPinViewModel
    lateinit var niInput: NIInput
    lateinit var texts: PinManagementResources
    lateinit var niPinFormType: NIPinFormType
    private lateinit var binding: ActivityViewPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED)
        intent.getSerializableExtraCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_INPUT} is missing")

        intent.getSerializableExtraCompat<NIPinFormType>(Extra.EXTRA_NI_PIN_FORM_TYPE)?.let {
            niPinFormType = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_TYPE} is missing")

        intent.getSerializableExtraCompat<PinManagementResources>(Extra.EXTRA_NI_PIN_FORM_RESOURCES)?.let {
            texts = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_RESOURCES} is missing")

        setTheme(ThemeHelper().getThemeResId(niInput))

        setArchitectureComponents()
        initializeUI()
    }

    private fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
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
        val viewPinFragment = ViewPinFragment.newInstance(niInput, texts)
        supportFragmentManager.beginTransaction().apply {
            add(binding.viewPinContainer.id, viewPinFragment, ViewPinFragment.TAG)
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
package ae.network.nicardmanagementsdk.presentation.ui.base_class

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.helpers.ThemeHelper
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivityWithPinForm<T : BaseViewModel> : AppCompatActivity() {

    lateinit var niInput: NIInput
    lateinit var niPinFormType: NIPinFormType
    abstract var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED)
        intent.getSerializableExtraCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_INPUT} is missing")

        intent.getSerializableExtraCompat<NIPinFormType>(Extra.EXTRA_NI_PIN_FORM_TYPE)?.let {
            niPinFormType = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_TYPE} is missing")

        setTheme(ThemeHelper().getThemeResId(niInput))
    }

    protected fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }
}
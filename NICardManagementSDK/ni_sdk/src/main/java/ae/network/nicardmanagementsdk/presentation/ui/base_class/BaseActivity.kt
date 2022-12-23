package ae.network.nicardmanagementsdk.presentation.ui.base_class

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity<T: BaseViewModel> : AppCompatActivity() {

    lateinit var niInput: NIInput
    abstract var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED)
        intent.getSerializableExtraCompat(Extra.EXTRA_NI_INPUT, NIInput::class.java)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_INPUT} is missing")

    }

    protected fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }
}
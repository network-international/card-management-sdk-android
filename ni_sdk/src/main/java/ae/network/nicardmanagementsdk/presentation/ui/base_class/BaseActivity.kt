package ae.network.nicardmanagementsdk.presentation.ui.base_class

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.helpers.ThemeHelper
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

abstract class BaseActivity<T: BaseViewModel> : AppCompatActivity() {

    lateinit var niInput: NIInput
    abstract var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED)
        intent.getSerializableExtraCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_INPUT} is missing")

        setTheme(ThemeHelper().getThemeResId(niInput))

        setLanguage(LanguageHelper().getLanguage(niInput))
    }

    override fun onResume() {
        super.onResume()
        setLanguage(LanguageHelper().getLanguage(niInput))
    }

    protected fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun setLanguage(language: String) {
        val res: Resources = resources
        val metrics = res.displayMetrics
        val config = res.configuration
        config.setLocale(Locale(language))
        res.updateConfiguration(config, metrics)
        onConfigurationChanged(config)
    }
}
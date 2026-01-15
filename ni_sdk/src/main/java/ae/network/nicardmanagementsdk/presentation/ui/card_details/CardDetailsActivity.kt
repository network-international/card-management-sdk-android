package ae.network.nicardmanagementsdk.presentation.ui.card_details

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.databinding.ActivityCardDetailsBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.helpers.ThemeHelper
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragment
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentListener
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardDetailsActivity : AppCompatActivity(), CardDetailsFragmentListener {

    private lateinit var binding: ActivityCardDetailsBinding
    lateinit var viewModel: CardDetailsViewModel

    lateinit var niInput: NIInput

    @DrawableRes
    private var backgroundImage: Int? = null

    private var navTitle: Int? = null
    private lateinit var config: CardElementsConfig
    private var paddingDp: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Moved from BaseActivity
        setResult(Activity.RESULT_CANCELED)

        intent.getSerializableExtraCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_INPUT} is missing")

        setTheme(ThemeHelper().getThemeResId(niInput))

        intent.getSerializableExtraCompat<Int>(Extra.EXTRA_NI_CARD_BACKGROUND)?.let {
            backgroundImage = it
        }

        intent.getSerializableExtraCompat<Int>(Extra.EXTRA_NI_CARD_NAVIGATION_TITLE)?.let {
            navTitle = it
        }

        intent.getSerializableExtraCompat<CardElementsConfig>(Extra.EXTRA_NI_CARD_ELEMENTS_CONFIG)?.let {
            config = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_CARD_ELEMENTS_CONFIG} is missing")

        intent.getSerializableExtraCompat<Int>(Extra.EXTRA_NI_FRAGMENT_TOP_PADDING)?.let {
            paddingDp = it
        }

        setArchitectureComponents()
        initializeUI()
    }

    // Moved from BaseActivity
    protected fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun setArchitectureComponents() {
        val factory = Injector.getInstance(this).provideCardDetailsViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[CardDetailsViewModel::class.java]

        // Replace DataBindingUtil.setContentView with ViewBinding.inflate
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // REMOVED: binding.lifecycleOwner = this (not needed with ViewBinding)
        // REMOVED: binding.viewModel = viewModel (not needed with ViewBinding)
    }

    private fun initializeUI() {
        // Set navigation title programmatically
        navTitle?.let {
            val title = binding.customBackNavigationView.context.getString(it)
            binding.customBackNavigationView.setTitle(title)
        }

        // Setup back button click listener
        binding.customBackNavigationView.setOnBackButtonClickListener {
            finish()
        }

        // Set background image programmatically (replaces DataBinding)
        backgroundImage?.let { imageRes ->
            binding.cardBackgroundImageView.setImageResource(imageRes)
        }

        // Add CardDetailsFragment
        val cardDetailsFragment = CardDetailsFragment.newInstance(
            niInput,
            copyToClipboardMessage = R.string.copied_to_clipboard_en,
            config
        )

        supportFragmentManager.beginTransaction().apply {
            add(binding.cardContainer.id, cardDetailsFragment, CardDetailsFragment.TAG)
            commit()
        }

        // Set padding programmatically (replaces: binding.apply { paddingTop = paddingDp })
        binding.cardDetailsRootLayout.setPadding(0, paddingDp, 0, 0)
    }

    override fun onCardDetailsFragmentCompletion(response: SuccessErrorResponse) {
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

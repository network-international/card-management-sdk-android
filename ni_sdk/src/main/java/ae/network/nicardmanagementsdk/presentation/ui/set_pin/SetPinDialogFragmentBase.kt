package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.api.models.input.PinMessageAttributes
import ae.network.nicardmanagementsdk.api.models.output.NICancelledResponse
import ae.network.nicardmanagementsdk.databinding.ActivitySetPinBinding
import ae.network.nicardmanagementsdk.helpers.LanguageHelper
import ae.network.nicardmanagementsdk.helpers.ThemeHelper
import ae.network.nicardmanagementsdk.presentation.adapters.BulletListAdapter
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*

abstract class SetPinDialogFragmentBase<T : SetPinViewModelBase> : DialogFragment() {

    private var _binding: ActivitySetPinBinding? = null
    protected val binding: ActivitySetPinBinding
        get() = _binding!!

    private lateinit var niPinFormType: NIPinFormType
    protected var successErrorCancelResponse = SuccessErrorCancelResponse(
        isSuccess = null,
        isError = null,
        isCancelled = NICancelledResponse()
    )
    lateinit var niInput: NIInput
    abstract var viewModel: T
    private var density = context?.resources?.displayMetrics?.density
    private var paddingDp: Int = 0

    abstract fun checkSubscriber(context: Context)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkSubscriber(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getSerializableCompat<NIPinFormType>(Extra.EXTRA_NI_PIN_FORM_TYPE)?.let {
            niPinFormType = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_TYPE} is missing")
        arguments?.getSerializableCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_INPUT} is missing")

        setStyle(STYLE_NO_FRAME, ThemeHelper().getThemeResId(niInput))

        arguments?.getInt(Extra.EXTRA_SET_PIN_FRAGMENT_TOP_PADDING)?.let {
            paddingDp = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_SET_PIN_FRAGMENT_TOP_PADDING} is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.activity_set_pin, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun initializeUI() {
        binding.shouldDefaultLanguage = when (LanguageHelper().getLanguage(niInput)) {
            "ar" -> false
            else -> true
        }

        binding.customBackNavigationView.setOnBackButtonClickListener {
            dismiss()
        }
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = BulletListAdapter()
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0

                this@SetPinDialogFragmentBase.viewModel.bulletItemsLiveData.observe(this@SetPinDialogFragmentBase) { itemModels ->
                    itemModels?.let {
                        (adapter as BulletListAdapter).setItems(it)
                    }
                }

                this@SetPinDialogFragmentBase.viewModel.updateBulletCellLiveEvent.observe(this@SetPinDialogFragmentBase) { index ->
                    index?.let {
                        (adapter as BulletListAdapter).notifyUpdate(it)
                    }
                }
            }

            paddingTop = paddingDp
        }

        viewModel.updateNIInput(niInput)
    }

    protected fun showSuccessErrorFragment(pinMessageAttributes: PinMessageAttributes, isSuccess: Boolean) {
        pinMessageAttributes.let {
            val layoutId = if (isSuccess) it.successAttributes.layoutId else it.errorAttributes.layoutId
            val buttonId = if (isSuccess) it.successAttributes.buttonResId else it.errorAttributes.buttonResId
            val fragment = SuccessErrorFragment.newInstance(layoutId, buttonId)
            childFragmentManager.beginTransaction().apply {
                replace(R.id.setPinRootLayout, fragment, SuccessErrorFragment.TAG)
                commit()
            }
        }
    }

    protected fun setViewModelData() {
        if (viewModel.bulletItemModels.isEmpty()) {
            viewModel.generateInitialState(niPinFormType.minSize, niPinFormType.maxSize)
        }
    }

    protected fun createPinBundle(input: NIInput, type: NIPinFormType): Bundle = Bundle().apply {
        putSerializable(Extra.EXTRA_NI_INPUT, input)
        putSerializable(Extra.EXTRA_NI_PIN_FORM_TYPE, type)
    }

    protected fun createPinWithPaddingBundle(
        input: NIInput,
        type: NIPinFormType,
        padding: Int = 0
    ): Bundle = Bundle().apply {
        putSerializable(Extra.EXTRA_NI_INPUT, input)
        putSerializable(Extra.EXTRA_NI_PIN_FORM_TYPE, type)
        putSerializable(Extra.EXTRA_SET_PIN_FRAGMENT_TOP_PADDING, padding)
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
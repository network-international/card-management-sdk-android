package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.api.models.input.PinManagementResources
import ae.network.nicardmanagementsdk.api.models.input.PinResultAttributes
import ae.network.nicardmanagementsdk.api.models.output.NICancelledResponse
import ae.network.nicardmanagementsdk.databinding.ActivitySetPinBinding
import ae.network.nicardmanagementsdk.helpers.ThemeHelper
import ae.network.nicardmanagementsdk.presentation.adapters.BulletListAdapter
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.extension_methods.setUIElementText
import ae.network.nicardmanagementsdk.presentation.models.Extra
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager

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
    lateinit var texts: PinManagementResources
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

        arguments?.getSerializableCompat<PinManagementResources>(Extra.EXTRA_NI_PIN_FORM_RESOURCES)?.let {
            texts = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_PIN_FORM_RESOURCES} is missing")

        setStyle(STYLE_NO_FRAME, ThemeHelper().getThemeResId(niInput))

        arguments?.getInt(Extra.EXTRA_NI_FRAGMENT_TOP_PADDING)?.let {
            paddingDp = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_FRAGMENT_TOP_PADDING} is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Replace DataBindingUtil.inflate with ViewBinding.inflate
        _binding = ActivitySetPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun initializeUI() {
        // Set padding programmatically (was: android:paddingTop="@{paddingTop}")
        binding.setPinRootLayout.setPadding(0, paddingDp, 0, 0)

        // Observe viewModel changes and update UI
        viewModel.navTitle.observe(viewLifecycleOwner) { title ->
            binding.customBackNavigationView.setUIElementText(title)
        }

        viewModel.screenTitle.observe(viewLifecycleOwner) { title ->
            binding.titleTextView.setUIElementText(title)
        }

        // Set back button click listener (was: no binding, handled in custom view)
        binding.customBackNavigationView.setOnBackButtonClickListener {
            dismiss()
        }

        // Setup number button click listeners (was: android:onClick="@{() -> viewModel.onNumberKeyTap(x)}")
        setupNumberButtons()

        // Setup image button click listeners
        binding.doneImageButton.setOnClickListener {
            viewModel.onDoneImageButtonTap()
        }

        binding.backspaceImageButton.setOnClickListener {
            viewModel.onBackspaceImageButtonTap()
        }

        // Setup RecyclerView for bullet items
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = BulletListAdapter()
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0
        }

        // Observe bullet items and update RecyclerView (was: binding.viewModel, binding.context)
        viewModel.bulletItemsLiveData.observe(viewLifecycleOwner) { itemModels ->
            itemModels?.let {
                (binding.recyclerView.adapter as BulletListAdapter).setItems(it)
            }
        }

        // Observe bullet cell updates
        viewModel.updateBulletCellLiveEvent.observe(viewLifecycleOwner) { index ->
            index?.let {
                (binding.recyclerView.adapter as BulletListAdapter).notifyUpdate(it)
            }
        }

        // Observe validation state for done button (was: android:clickable="@{viewModel.validationSendButton}")
        viewModel.validationSendButton.observe(viewLifecycleOwner) { isValid ->
            binding.doneImageButton.isClickable = isValid
            binding.doneImageButton.isFocusable = isValid
            // Update button visual state based on validation
            updateDoneButtonState(isValid)
        }

        // Observe progress bar visibility (was: android:visibility="@{viewModel.isVisibleProgressBar...}")
        viewModel.isVisibleProgressBar.observe(viewLifecycleOwner) { isVisible ->
            binding.overlayFrameLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            binding.oneButton to 1,
            binding.twoButton to 2,
            binding.threeButton to 3,
            binding.fourButton to 4,
            binding.fiveButton to 5,
            binding.sixButton to 6,
            binding.sevenButton to 7,
            binding.eightButton to 8,
            binding.nineButton to 9,
            binding.zeroButton to 0
        )

        numberButtons.forEach { (button, number) ->
            button.setOnClickListener {
                viewModel.onNumberKeyTap(number)
            }
        }
    }

    private fun updateDoneButtonState(isValid: Boolean) {
        // Update visual appearance of done button based on validation state
        // You can customize this based on your custom binding adapters (useDisabledBgTint, useDisabledColor)
        binding.doneImageButton.alpha = if (isValid) 1.0f else 0.5f
        binding.doneImageButton.isEnabled = isValid
    }

    protected fun showSuccessErrorFragment(pinResultAttributes: PinResultAttributes, isSuccess: Boolean) {
        pinResultAttributes.let {
            val layoutId = if (isSuccess) it.successScreen.layoutId else it.errorScreen.layoutId
            val buttonId = if (isSuccess) it.successScreen.buttonResId else it.errorScreen.buttonResId
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

    protected fun createPinWithPaddingBundle(
        input: NIInput,
        type: NIPinFormType,
        texts: PinManagementResources,
        padding: Int = 0
    ): Bundle = Bundle().apply {
        putSerializable(Extra.EXTRA_NI_INPUT, input)
        putSerializable(Extra.EXTRA_NI_PIN_FORM_TYPE, type)
        putSerializable(Extra.EXTRA_NI_FRAGMENT_TOP_PADDING, padding)
        putSerializable(Extra.EXTRA_NI_PIN_FORM_RESOURCES, texts)
    }
}

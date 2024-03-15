# Network International Android Card Management SDK

The Network International Android Card Management SDK allows you to integrate with Network International standard APIs for card management. (https://developer.network.ae)
The current supported features are:
1. Get Secured Card Details : Display a card component providing the ability to show card number, expiry date, CVV and cardholder name. This supports full end to end encryption to securely transport this sensitive information.
2. Set PIN : Displays a PIN pad to allow cardholder to set a PIN on their new card. The PIN is end to end encrypted to securely transport this sensitive information
3. Change PIN: Displays two PIN pad to allow the cardholder to change their PIN by providing old & new PIN. The PINs are end to end encrypted to securely transport this sensitive information



## Integration

### Basics
After you have installed the SDK, by following one of the above set of steps, you can import the SDK into your Android app and used it.
### Sample usage:
Kotlin:
```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val niInput: NIInput
    private val pinLength: NIPinFormType
    
    // Optional paddingTop, can only be applied for Set/Change/Verify PIN screens.
    // The value (of Int type) must be passed to the get() method
    // To complete the padding customization, the paddingTop parameter must be passed to desired fragment (alongside niInput and pinLength)
    // If there's no paddingTop parameter sent, there will be no paddingTop.
    private val paddingTop: Int
        get() = 100

    // Create an instance of NICardManagement. Callback for completion handler are provided here
    private val niCardManagementForms = NICardManagementForms(
        this,
        displayCardDetailsOnCompletion = getCompletionHandler("displayCardDetailsForm"),
        setPinOnCompletion = getCompletionHandler("setPinForm"),
        changePinOnCompletion = getCompletionHandler("changePinForm")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUI()
    }

    private fun initializeUI() {
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = EntriesListAdapter()
                setHasFixedSize(true)
            }

            cardDetailsButton.setOnClickListener {
                // Build NIInput Object
                niCardManagementForms.displayCardDetailsForm(niInput)
            }

            setPinButton.setOnClickListener {
                // Build NIInput Object & PIN Length
                niCardManagementForms.setPinForm(niInput, pinLength)
            }

            changePinButton.setOnClickListener {
                // Build NIInput Object & PIN Length
                niCardManagementForms.changePinForm(niInput, pinLength)
            }
        }
    }

    // 
    private fun getCompletionHandler(formName: String): OnSuccessErrorCancelCompletion =
        { success, error, canceled ->
            if (canceled) {
                Log.d(TAG, "$formName canceled by the user")
            } else {
                success?.let {
                    Log.d(TAG, "$formName OK")
                }
                error?.let {
                    Log.d(TAG, "$formName execution has error")
                }
            }
        }

    companion object {
        const val TAG = "MainActivity"
    }
}
```

### Types
Various Types have been defined to work as input to our SDK
```kotlin
/* This is the main class wrapping all the input required for our SDK
@bankCode: The Financial-Id provided to the bank to integrate with our API
@cardIdentifierId: The ID of the card for which the action needs to be taken
@cardIdentifierType: The type of ID provided above (e.g. EXID)
@connectionProperties: The ROOT URL & Auth Token to reach your proxy services.
@displayAttributes: Customizable parameters allowing to personalize the display of the UI
*/
data class NIInput(
    val bankCode: String,
    val cardIdentifierId: String,
    val cardIdentifierType: String,
    val connectionProperties: NIConnectionProperties,
    val displayAttributes: NIDisplayAttributes? = null
) : Serializable

/*
@rootUrl: The URL which will prefix any of our API calls pointing to your implementation of proxy services. e.g. https://api.mybank.com/ will result in: https://api.mybank.com/cards/secured
@token: The oAuth2 token used to identify this App with the customers endpoint (Not Network International) which will be passed in the Authorization header field.
*/
data class NIConnectionProperties(
    val rootUrl: String,
    val token: String
) : Serializable

/*
@fonts: Allows redefinition of font size for every label
@cardAttributes: Allows redefinition of the background image with customer own design and whether the sensitive data should be visible by default or hidden behind an 'Eye' icon to reveal on demand.
@setPinMessageAttributes, 
@verifyPinMessageAttributes,
@changePinMessageAttributes : Allows displaying of a custom layout on completion, for success/error screen
@theme : Allow to display the status bar following your current theme setting
*/
data class NIDisplayAttributes(
    // this parameter is optional
    // if set, these fonts will be used in the UI forms; if not set will use default fonts
    val fonts: List<NIFontLabelPair>? = null,

    // this parameter is optional
    // if set, the card details will take into account the attributes passed into this variable
    // if not set, will take the default values
    val cardAttributes: NICardAttributes? = null,

    // the next three parameter are optional
    // if set an custom layout will be displayed on completion, and the component will navigate
    // back on "doneButton" specified as an @IdRes val buttonResId: Int
    val setPinMessageAttributes: PinMessageAttributes? = null,
    val verifyPinMessageAttributes: PinMessageAttributes? = null,
    val changePinMessageAttributes: PinMessageAttributes? = null,

    // This parameter is optional.
    // If not set the SDK will follow your parent app day/night mode based on OS settings or as requested by your app.
    // The recommended way for using this parameter is to leave it unset, unless you have some special requirements.
    // If a value is set, the SDK will emulate (force) day/night mode, regardless of the system settings.
    val theme: NITheme? = null
): Serializable

enum class NITheme: Serializable {
    LIGHT, DARK_APP_COMPAT, DARK_MATERIAL
}

data class NIFontLabelPair(
    val uiFont: UIFont,
    var label: NILabels
): Serializable

data class UIFont(
    @FontRes
    val fontRes: Int? = null,
    // interpreted as "scaled pixel" units Sp
    val textSize: Int
): Serializable

enum class NILabels: Serializable {

    // Card Details
    CARD_NUMBER_LABEL,
    CARD_NUMBER_VALUE_LABEL,
    EXPIRY_DATE_LABEL,
    EXPIRY_DATE_VALUE_LABEL,
    CVV_LABEL,
    CVV_VALUE_LABEL,
    CARD_HOLDER_NAME_LABEL,
    CARD_HOLDER_NAME_VALUE_LABEL,

    // Set PIN
    SET_DESCRIPTION_LABEL,

    // Change PIN
    CHANGE_PIN_DESCRIPTION_LABEL
}

data class NICardAttributes(
    // if true, the card details will be hidden/masked by default; if false, the card details will be visible by default
    val shouldHide: Boolean = true,
    // if set, this image will be used as background for the card details view; if not set, it will use default image from sdk
    @DrawableRes
    val backgroundImage: Int? = null,
    // if set will apply new text positioning values for Card Details components: cardNumberLine, dateCvvLine and cardHolderNameLine
    // if not set default positioning will be used
    val textPositioning: TextPositioning? = null
): Serializable

// the ratio of the parent container width/height in the range 0..1 (meaning 0 to 100% percent)
// relative to the left|top corner (which is the axis origin point)
// leftAlignment: all three groups of views (lines) have the same left alignment
// cardNumberGroupTopAlignment: card number line top alignment
// dateCvvGroupTopAlignment: date cvv line top alignment
// cardHolderNameGroupTopAlignment: card holder name line top alignment
data class TextPositioning(
    val leftAlignment: Float? = null,
    val cardNumberGroupTopAlignment: Float? = null,
    val dateCvvGroupTopAlignment: Float? = null,
    val cardHolderNameGroupTopAlignment: Float? = null
): Serializable

data class PinMessageAttributes(
    val successAttributes: SuccessErrorScreenAttributes,
    val errorAttributes: SuccessErrorScreenAttributes
): Serializable

data class SuccessErrorScreenAttributes(
    @LayoutRes val layoutId: Int,
    @IdRes val buttonResId: Int
): Serializable


// Allowed length of PIN. Recommended to use FOUR_DIGITS as per industry standards.
enum class NIPinFormType(val minSize: Int, val maxSize: Int) {
    DYNAMIC (4, 6),
    FOUR_DIGITS (4, 4),
    FIVE_DIGITS (5, 5),
    SIX_DIGITS (6, 6)
}

```

### Display a fragment
```kotlin
class CardUsageDemoActivity : AppCompatActivity(), CardDetailsFragment.OnFragmentInteractionListener {
    lateinit var niInput: NIInput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_usage_demo)
        initializeUI()
    }

    private fun initializeUI() {
        // TODO Build niInput here
        val cardDetailsFragment = CardDetailsFragment.newInstance(niInput)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.card_container, cardDetailsFragment, CardDetailsFragment.TAG)
            commit()
        }
    }
}
```
The UI of the activity should offer a container described here by R.id.card_container, which have the appropiate width dimension. The fragment component will take the width of the container to resize itself, and the height is enforced by the aspect ratio of 16:10 (width:height), which guarantee a good user experience related to card UI.

Below is an example of layout from the demo activity:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".CardUsageDemoActivity">

    <ae.network.nicardmanagementsdk.presentation.views.CustomBackNavigationView
        android:id="@+id/customBackNavigationView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:title="@string/card_details_from_custom_component" />

    <TextView
        android:id="@+id/firstTitleTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:layout_marginBottom="24dp"
        android:text="@string/custom_component_used_fom_host_activity"
        android:textAlignment="center"
        android:textAppearance="@style/TextAppearance.MaterialComponents.Subtitle1"
        android:textStyle="bold|italic"
        app:layout_constraintBottom_toTopOf="@+id/card_container"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/customBackNavigationView"
        app:layout_constraintVertical_bias="1.0" />

    <FrameLayout
        android:id="@+id/card_container"
        android:layout_width="335dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.5"
        tools:background="@color/blue1"
        tools:minHeight="209dp">

    </FrameLayout>

    <androidx.constraintlayout.widget.Guideline
        android:id="@+id/guideline3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintGuide_percent="1.0" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Display a card details fragment with information about position of each element
Refer to `CardUsageDemoActivity.kt` with example
For custom elements position `CardDetailsFragmentFreeForm` can be used
```kotlin
val cardDetailsFragment = CardDetailsFragmentFreeForm.newInstance(
            niInput,
            elementsColor = colorPrimary,
            positioning = CardElementsPositioning(
                cardNumberLabel = CardElementPositioning(start = 16, top = 16),
                cardNumberText = CardElementPositioning(start = 200, top = 16),
                cardNumberButton = CardElementPositioning(start = 700, top = 16),
                cardHolderLabel = CardElementPositioning(start = 16, top = 196),
                cardHolderText = CardElementPositioning(start = 200, top = 196),
                cardHolderButton = CardElementPositioning(start = 700, top = 196),
                expiryLabel = CardElementPositioning(start = 16, top = 76),
                expiryText = CardElementPositioning(start = 200, top = 76),
                showDetailsButton = CardElementPositioning(start = 700, top = 76),
                cvvLabel = CardElementPositioning(start = 16, top = 136),
                cvvText = CardElementPositioning(start = 200, top = 136)
            )
        )
```
- `positioning` Each element can be positioned with start/top dimentsions related to parent
- `elementsColor` set color for card elements

And this fragment should be placed into container
```kotlin
add(R.id.card_container, cardDetailsFragment, CardDetailsFragmentFreeForm.TAG)
```

### Verify PIN from an activity
```kotlin
class MainActivity : AppCompatActivity(), VerifyPinFragment.OnFragmentInteractionListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val niInput: NIInput
        get() = makeInputObject()
    private val pinLength: NIPinFormType
        get() = viewModel.getPINLength()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setArchitectureComponents()
        initializeUI()
    }

    private fun setArchitectureComponents() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initializeUI() {
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = EntriesListAdapter()
                setHasFixedSize(true)

                this@MainActivity.viewModel.entriesItemsLiveData.observe(this@MainActivity) { itemModels ->
                    itemModels?.let {
                        (adapter as EntriesListAdapter).setItems(it)
                    }
                }
            }

            verifyPinButton.setOnClickListener {
                val dialog = VerifyPinFragmentFromActivity.newInstance(niInput, pinLength)
                dialog.show(supportFragmentManager, VerifyPinFragmentFromActivity.TAG)
            }
        }
    }

    private fun makeInputObject(): NIInput {
        return NIInput(
            bankCode = viewModel.entriesItemModels.first { model -> model.id == BANK_CODE }.value,
            cardIdentifierId = viewModel.entriesItemModels.first { model -> model.id == CARD_ID }.value,
            cardIdentifierType = viewModel.entriesItemModels.first { model -> model.id == CARD_TYPE }.value,
            connectionProperties = NIConnectionProperties(
                viewModel.entriesItemModels.first { model -> model.id == ROOT_URL }.value,
                viewModel.entriesItemModels.first { model -> model.id == TOKEN }.value,
            ),
            displayAttributes = NIDisplayAttributes(
                verifyPinMessageAttributes = PinMessageAttributes(
                    successAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_success,
                        buttonResId = R.id.doneButton
                    ),
                    errorAttributes = SuccessErrorScreenAttributes(
                        layoutId = R.layout.activity_error,
                        buttonResId = R.id.doneButton
                    )
                )
            )
        )
    }

    companion object {
        const val TAG = "SDKLogMessage"
    }

    override fun onVerifyPinFragmentCompletion(response: SuccessErrorResponse) {
        response.isSuccess?.let {
            Log.d(TAG, "VerifyPinFragmentFromActivity ${it.message}")
        }

        response.isError?.let {
            Log.d(TAG, "VerifyPinFragmentFromActivity ${it.errorMessage}")
        }
    }

}
```
The activity should implement VerifyPinFragment.OnFragmentInteractionListener interface in order to receive
the success/error operation callback, then you have to prepare an NIInput and NIPinFormType objects.
In the current example the VerifyPinFragmentFromActivity component is displayed on screen using the
verifyPinButton.setOnClickListener {}.
A description of the NIPinFormType is as following:

```kotlin
enum class NIPinFormType(val minSize: Int, val maxSize: Int) {
    DYNAMIC(4, 6),
    FOUR_DIGITS(4, 4),
    FIVE_DIGITS (5, 5),
    SIX_DIGITS (6, 6)
}
```
and a pinLength parameter example could be :

```kotlin
pinLength = NIPinFormType.FOUR_DIGITS
```
The NIInput class has an optional "val displayAttributes: NIDisplayAttributes? = null" constructor
parameter, which in turn has an optional "val verifyPinMessageAttributes: PinMessageAttributes? = null"
parameter of type PinMessageAttributes.

If you do not specify an "verifyPinMessageAttributes" the VerifyPin component will navigate back
automatically after completion, but if you give these "verifyPinMessageAttributes" optional parameter
to the NIInput object, the component will display for you a custom success and error layout,
and will navigate back on "buttonResId" button setOnClickListener{}, giving the user the opportunity
to display a custom success/error layout with a "done" button.

### Verify PIN from a fragment
```kotlin
class MainFragment : Fragment(), VerifyPinFragment.OnFragmentInteractionListener {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding!!

    private lateinit var niInput: NIInput
    private lateinit var niPinFormType: NIPinFormType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializableCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        } ?: throw RuntimeException("${this::class.java.simpleName} arguments serializable ${Extra.EXTRA_NI_INPUT} is missing")

        arguments?.getSerializableCompat<NIPinFormType>(Extra.EXTRA_NI_PIN_FORM_TYPE)?.let {
            niPinFormType = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_TYPE} is missing")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.verifyPinButton.setOnClickListener {
            val dialog = VerifyPinFragmentFromFragment.newInstance(niInput, niPinFormType)
            dialog.show(childFragmentManager, VerifyPinFragmentFromFragment.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    

    override fun onVerifyPinFragmentCompletion(response: SuccessErrorResponse) {
        response.isSuccess?.let {
            Log.d(MainActivity.TAG, "VerifyPinFragmentFromFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(MainActivity.TAG, "VerifyPinFragmentFromFragment ${it.error}  ${it.errorMessage}")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(input: NIInput, type: NIPinFormType) = MainFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Extra.EXTRA_NI_INPUT, input)
                putSerializable(Extra.EXTRA_NI_PIN_FORM_TYPE, type)
            }
        }

        const val TAG = "MainFragment"
    }
}
```
When using VerifyPin component from a fragment, the fragment should implement VerifyPinFragment.OnFragmentInteractionListener
in order to receive the success/error callback. Please take note of using SetPinFragmentFromFragment
component and offer a childFragmentManager parameter value to the dialog.show() method:

```kotlin
binding.setPinButton.setOnClickListener {
            val dialog = SetPinFragmentFromFragment.newInstance(niInput, niPinFormType)
            dialog.show(childFragmentManager, SetPinFragmentFromFragment.TAG)
        }
```
All other aspects are the same as for SetPinFragmentFromActivity component

### ProGuard rules
If you are using minifyEnabled true for your build configuration:
```groovy
buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
```
you have to add the following line of code to your proguard-rules.pro configuration file, in order to successfully generate the X.509 certificate:

```groovy
-keep class org.bouncycastle.** { *; }
```

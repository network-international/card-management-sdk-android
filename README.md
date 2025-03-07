# Network International Android Card Management SDK

The Network International Android Card Management SDK allows you to integrate with Network International standard APIs for card management. (https://developer.network.ae)
The current supported features are:
1. Get Secured Card Details : Display a card component providing the ability to show card number, expiry date, CVV and cardholder name. This supports full end to end encryption to securely transport this sensitive information.
2. Set PIN : Displays a PIN pad to allow cardholder to set a PIN on their new card. The PIN is end to end encrypted to securely transport this sensitive information
3. Change PIN: Displays two PIN pad to allow the cardholder to change their PIN by providing old & new PIN. The PINs are end to end encrypted to securely transport this sensitive information



## Integration

### Basics
After you have installed the SDK, by following one of the above set of steps, you can import the SDK into your Android app and used it.
https://jitpack.io/#network-international/card-management-sdk-android

### Sample usage:
Check Sample application for details
Kotlin:
```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val niInput: NIInput
    private val pinLength: NIPinFormType
    private val pinFlowResources = PinManagementResources.default(
        setPinResultAttributes = makePinResultAttributes(),
        verifyPinMessageAttributes = makePinResultAttributes(),
        changePinResultAttributes = makePinResultAttributes(),
    )
    
    // Optional paddingTop, can only be applied for Set/Change/Verify PIN screens.
    // The value (of Int type) must be passed to the get() method
    // To complete the padding customization, the paddingTop parameter must be passed to desired fragment (alongside niInput and pinLength)
    // If there's no paddingTop parameter sent, there will be no paddingTop.
    private val paddingTop: Int
        get() = 100

    // Create an instance of NICardManagement. Callback for completion handler are provided here
    private val niCardManagementForms = NICardManagementForms(
        this,
        displayCardDetailsOnCompletion = getCompletionHandler("displayCardDetailsForm")
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
                niCardManagementForms.displayCardDetailsForm(
                    niInput,
                    backgroundImage = ae.network.nicardmanagementsdk.R.drawable.bg_default_mc,
                    title = ae.network.nicardmanagementsdk.R.string.card_details_title_en,
                    config = CardElementsConfig.default(
                        copyTargets = listOf<CardMaskableElement>(
                            CardMaskableElement.CARDNUMBER,
                            CardMaskableElement.CARDHOLDER,
                        ),
                        copyTemplate = "Card number: %s\nName: %s"
                    )
                )
            }

            setPinButton.setOnClickListener {
                // provide padding for pin forms
                val dialog = SetPinFragment.newInstance(niInput, pinLength, pinFlowResources, padding = 100)
                dialog.show(supportFragmentManager, SetPinFragment.TAG)
            }

            verifyPinButton.setOnClickListener {
                val dialog = VerifyPinFragment.newInstance(niInput, pinLength, pinFlowResources)
                dialog.show(supportFragmentManager, VerifyPinFragment.TAG)
            }

            changePinButton.setOnClickListener {
                val dialog = ChangePinFragment.newInstance(niInput, pinLength, pinFlowResources)
                dialog.show(supportFragmentManager, ChangePinFragment.TAG)
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
@extraNetworkHeaders: Map<String, String> key-value pairs that will be used as additional http headers
*/
data class NIConnectionProperties(
    val rootUrl: String,
    val token: String,
    val extraNetworkHeaders: Map<String, String>? = null
) : Serializable

data class NIDisplayAttributes(
    // This parameter is optional.
    // If not set the SDK will follow your parent app day/night mode based on OS settings or as requested by your app.
    // The recommended way for using this parameter is to leave it unset, unless you have some special requirements.
    // If a value is set, the SDK will emulate (force) day/night mode, regardless of the system settings.
    val theme: NITheme? = null
): Serializable

// Provide texts for labels, 
// styles and grapphic resources for UI elements, 
// layout information for positioning of UI elements
data class CardElementsConfig(
    var cardNumber: CardElementsItemConfig? = null,
    var expiry: CardElementsItemConfig? = null,
    var cvv: CardElementsItemConfig? = null,
    var cardHolder: CardElementsItemConfig? = null,
    // common mask button - toggle all elements together
    var commonMaskButton: CardElementMaskButton? = null, // common show details button
    // define initial state of masking
    var shouldBeMaskedDefault: List<CardMaskableElement> = listOf(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.EXPIRY,
        CardMaskableElement.CVV,
        CardMaskableElement.CARDHOLDER,
    ),
    // if not null - standard progressIndicator shows progress
    var progressBar: CardProgressBarConfig? = null,
): Serializable

data class CardPresenterConfig(
    var cardNumber: CardPresenterElementConfig?,
    var expiry: CardPresenterElementConfig?,
    var cvv: CardPresenterElementConfig?,
    var cardHolder: CardPresenterElementConfig?,
    var shouldBeMaskedDefault: List<CardMaskableElement>
): Serializable

// Provide strings for PIN management screens
// optionally set layoutId in `PinResultAttributes` for showing PIN operation results screen
data class PinManagementResources (
    var setPin: PinManagementSetPinResources,
    var verifyPin: PinManagementVerifyPinResources,
    var changePin: PinManagementChangePinResources,
    var viewPin: PinManagementViewPinResources,
): Serializable

data class PinResultAttributes(
    val successScreen: PinResultScreenAttributes,
    val errorScreen: PinResultScreenAttributes
): Serializable

data class PinResultScreenAttributes(
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

### Customization of card details UI (Card details fragment or activity) - CardElementsConfig
Refer to `CardUsageDemoActivity.kt` or to the `config` parameter for `niCardManagementForms.displayCardDetailsForm` in `MainActivity.kt` with example

Alternate `CardElementsConfig` that will be passed to fragment or form
```kotlin
// You can use default configuration and change it, or build it from scratch
// In this example - use default and set behaviour of `copy` button
// by copy action - given fields with given template will be copied to clipboard
var cardElementsConfig = CardElementsConfig.default(
    copyTargets = listOf<CardMaskableElement>(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.CARDHOLDER,
    ),
    copyTemplate = "Card number: %s\nName: %s"
)
```
##### update position of Card element, example with card number label:
```kotlin
// update position if needed - attach element to bottom-left corner
config.cardNumber?.label?.layout = CardElementLayout(bottom = 0, left = 0)
```
##### update text of Card element label, example with card number label:
- Use either StringRes or String for labels and for button's content descrition
```kotlin
config.cardNumber?.label?.text  = CardElementText.String("My card #")
// OR
config.cardNumber?.label?.text  = CardElementText.Int(ae.network.nicardmanagementsdk.R.string.card_details_title_en)
```
##### provide desired appearance style to update font/color... of desired element 
```kotlin
config.cardNumber?.label?.appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberLabel
config.cardNumber?.details?.appearanceResId = R.style.TextAppearance_NICardManagementSDK_CardElement_CardNumberData
```
##### configure copy action behaviour - define card details values and format that will be passed to clipboard
```kotlin
copyButton = CardElementCopyButton( // use null to hide button
    imageDefault = R.drawable.ic_baseline_content_copy,
    layout = CardElementLayout(left = 510, top = 170),
    targets = listOf<CardMaskableElement>(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.CARDHOLDER,
    ),
    template = "Card number: %s\nName: %s",
    contentDescription = R.string.copy_to_clipboard_image_content_description
)
```
##### provide icons that will be used for desired buttons, use desired sizes and colors
```kotlin
imageDefault = R.drawable.ic_reveal_details
imageSelected = R.drawable.ic_hide_details
```

Other configurations is also available within `CardElementsConfig`

### Display a card details fragment

```kotlin
val cardDetailsFragment = CardDetailsFragment.newInstance(
    niInput,
    // Only show a toast for Android 12 and lower.
    copyToClipboardMessage = R.string.copied_to_clipboard_en,
    cardElementsConfig
)
supportFragmentManager.beginTransaction().apply {
    add(R.id.card_container, cardDetailsFragment, CardDetailsFragment.TAG)
    commit()
}
```

The UI of the activity should offer a container described here by R.id.card_container, which have the appropiate width dimension. The fragment component will take the width of the container to resize itself, and the height is enforced by the aspect ratio of 16:10 (width:height), which guarantee a good user experience related to card UI.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".CardUsageDemoActivity">

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

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Compose your custom layour for Card details with `CardElementsPresenter` usage
Refer to `CardBottomSheetDialogFragment.kt` with example of how to create and use `presenter`
- Note: Presenter do not hold any buttons, you should implemented any required buttons in your layout and conntect actions to presenter's methods

##### Alternate `CardPresenterConfig` to update style of presenter
- similarly to `CardElementsConfig`
- allows to provide desired texts and appearance style (font/color) for card elements
- do not include configuration for any buttons, as buttons should be implemented in custom layout if needed
```kotlin
data class CardPresenterConfig(
    val cardNumber: CardPresenterElementConfig?,
    val expiry: CardPresenterElementConfig?,
    val cvv: CardPresenterElementConfig?,
    val cardHolder: CardPresenterElementConfig?,
    var shouldBeMaskedDefault: List<CardMaskableElement>
): Serializable
```
```kotlin
val presenterConfig = CardPresenterConfig.default()
```

##### Instantiate presenter in your fragment / activity
```kotlin
val presenterConfig = CardPresenterConfig.default()
presenterConfig.shouldBeMaskedDefault = listOf<CardMaskableElement>(CardMaskableElement.CVV)
val presenter = CardElementsPresenter.newInstance(requireContext(), this, niInput, presenterConfig)
```
##### Insert card elements into your layout containers
- you can combine your own labels with card details values and skip presenter's labels if needed
```kotlin
binding.cardNumberValueHolder.addView(presenter.cardNumber.data)
```
- connect your buttons with presenter's methods
```kotlin
// define fields for copy to clipboard
binding.btnCopy.setOnClickListener {
    val copyTargets = listOf<CardMaskableElement>(
        CardMaskableElement.CARDNUMBER,
        CardMaskableElement.CARDHOLDER,
    )
    val copyTemplate = "Card number: %s\nName: %s"
    val clipboardManager =
        requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    presenter.copyToClipboard(copyTargets, copyTemplate, clipboardManager, R.string.copied_to_clipboard_en)
}
```
- initiate card details request
```kotlin
presenter.fetch()
```
- check `CardBottomSheetDialogFragment` layout for the example
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#2196F3"
    android:padding="24dp"
    tools:context="ae.network.nicardmanagementsdk.sample.CardBottomSheetDialogFragment">

    <!--Use your own card labels if needed-->
    <TextView
        android:id="@+id/numberLabel"
        style="@style/TextAppearance.AppCompat.Caption"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:text="Card #"
        app:layout_constraintStart_toStartOf="parent" />
    <!--Use your own copy button-->
    <ImageView
        android:id="@+id/btnCopy"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingStart="8dp"
        android:contentDescription="@string/copy_to_clipboard_image_content_description"
        android:src="@drawable/ic_baseline_content_copy"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="@+id/numberLabel"
        tools:ignore="RtlSymmetry" />
    <!--    Provide containers for card values, card labels can be ignored or use your own labels-->
    <FrameLayout android:id="@+id/cardNumberValueHolder" android:layout_width="wrap_content" android:layout_height="wrap_content"
        app:layout_constraintEnd_toStartOf="@+id/btnCopy"
        app:layout_constraintBottom_toBottomOf="@+id/numberLabel"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### PIN management
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

            val pinFlowResources = PinManagementResources.default(
                setPinResultAttributes = makePinResultAttributes(),
                verifyPinMessageAttributes = makePinResultAttributes(),
                changePinResultAttributes = makePinResultAttributes(),
            )
            setPinButton.setOnClickListener {
                // provide padding for pin forms
                val dialog = SetPinFragment.newInstance(niInput, pinLength, pinFlowResources, padding = 100)
                dialog.show(supportFragmentManager, SetPinFragment.TAG)
            }

            verifyPinButton.setOnClickListener {
                val dialog = VerifyPinFragment.newInstance(niInput, pinLength, pinFlowResources)
                dialog.show(supportFragmentManager, VerifyPinFragment.TAG)
            }

            changePinButton.setOnClickListener {
                val dialog = ChangePinFragment.newInstance(niInput, pinLength, pinFlowResources)
                dialog.show(supportFragmentManager, ChangePinFragment.TAG)
            }
        }
    }

    private fun makePinResultAttributes(): PinResultAttributes {
        return PinResultAttributes(
            successScreen = PinResultScreenAttributes(
                layoutId = R.layout.activity_success,
                buttonResId = R.id.doneButton
            ),
            errorScreen = PinResultScreenAttributes(
                layoutId = R.layout.activity_error,
                buttonResId = R.id.doneButton
            )
        )
    }

    private fun makeInputObject(): NIInput {
        return NIInput(
            bankCode = viewModel.entriesItemModels.first { model -> model.id == BANK_CODE }.value,
            cardIdentifierId = viewModel.entriesItemModels.first { model -> model.id == CARD_ID }.value,
            cardIdentifierType = viewModel.entriesItemModels.first { model -> model.id == CARD_TYPE }.value,
            connectionProperties = NIConnectionProperties(
                viewModel.entriesItemModels.first { model -> model.id == ROOT_URL }.value,
                viewModel.entriesItemModels.first { model -> model.id == TOKEN }.value,
                extraNetworkHeaders = hashMapOf(
                    "extraHeader1" to "DemoExtraHttpHeaderValue",
                    "Content-Type" to "will be ignored for existing header" // this will be ignored
                )
            ),
            displayAttributes = NIDisplayAttributes(
                //theme = NITheme.DARK_APP_COMPAT
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
The activity should implement 
VerifyPinFragment.OnFragmentInteractionListener,
SetPinFragment.OnFragmentInteractionListener,
ChangePinFragment.OnFragmentInteractionListener,
interface in order to receive
the success/error operation callback
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
Check an optional `PinResultAttributes` - If you do not specify an attributes the PinManagement component will navigate back
automatically after completion, 
otherwise the component will display for you a custom success and error layout,
and will navigate back on "buttonResId" button setOnClickListener{}, giving the user the opportunity
to display a custom success/error layout with a "done" button.

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
-keepclassmembers class ae.network.nicardmanagementsdk.** { *; }
```
If some of your third party libraries relies on reflection so proguard is breaking it.
If you add that line in your proguard file, you should have the Class name and line number instead of `Unkown Source` in the stack trace:
```groovy
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
```

#### R8 full mode ProGuard Troubleshooting
find instructions in documentation
https://r8.googlesource.com/r8/+/refs/heads/master/compatibility-faq.md#troubleshooting
# Network International Android Card Management SDK

The Network International Android Card Management SDK allows you to integrate with Network International standard APIs for card management. (https://developer.network.ae)
The current supported features are:
1. Get Secured Card Details : Display a card component providing the ability to show card number, expiry date, CVV and cardholder name. This supports full end to end encryption to securely transport this sensitive information.
2. Set PIN : Displays a PIN pad to allow cardholder to set a PIN on their new card. The PIN is end to end encrypted to securely transport this sensitive information
3. Change PIN: Displays two PIN pad to allow the cardholder to change their PIN by providing old & new PIN. The PINs are end to end encrypted to securely transport this sensitive information



## Integration

### Basics
After you have installed the SDK, by following one of the above set of steps, you can import the SDK into your iOS app and used it.
### Sample usage:
Kotlin:
```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val niInput: NIInput
    private val pinLength: NIPinFormType

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
                niCardManagementForms.setPinForm( niInput, pinLength)
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
@theme: LIGHT or DARK theme
@language: ENGLISH or ARABIC
@fonts: Allows redefinition of font size for every label
@cardAttributes: Allows redefinition of the background image with customer own design and whether the sensitive data should be visible by default or hidden behind an 'Eye' icon to reveal on demand.
*/
data class NIDisplayAttributes(
    // is always required
    val theme: NITheme,
    // if language is not set, the sdk will use the device language (english or arabic), other languages will default to english
    val language: NILanguage? = null,
    // if set, these fonts will be used in the UI forms; if not set will use default fonts
    val fonts: List<NIFontLabelPair>? = null,
    // if set, the card details will take into account the attributes passed into this variable; if not set, will take the default values
    val cardAttributes: NICardAttributes? = null
): Serializable

enum class NITheme: Serializable {
    LIGHT, DARK
}

enum class NILanguage(val shortName : String): Serializable {
    ENGLISH("en"),
    ARABIC("ar")
}

data class NIFontLabelPair(
    @FontRes
    val fontRes: Int,
    var label: NILabels
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

    // Set PIN
    SET_DESCRIPTION_LABEL,

    // Change PIN
    CHANGE_PIN_DESCRIPTION_LABEL
}

data class NICardAttributes(
    // if true, the card details will be hidden/masked by default; if false, the card details will be visible by default
    val shouldHide: Boolean = true,
    // if set, this image will be used as background for the card details view; if not set, it will use default image from sdk
    val backgroundImage: Drawable? = null
)

/*
    Allowed length of PIN. Recommended to use FOUR_DIGITS as per industry standards.
*/
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
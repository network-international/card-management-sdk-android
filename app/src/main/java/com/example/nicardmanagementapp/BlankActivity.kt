package com.example.nicardmanagementapp

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.views.CustomBackNavigationView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BlankActivity : AppCompatActivity() {
    private lateinit var niInput: NIInput
    private lateinit var niPinFormType: NIPinFormType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)
        getDataFromBundle()
        initializeUI()
    }


    private fun getDataFromBundle() {
        intent.getSerializableExtraCompat<NIInput>(Extra.EXTRA_NI_INPUT)?.let {
            niInput = it
        }
            ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_INPUT} is missing")

        intent.getSerializableExtraCompat<NIPinFormType>(Extra.EXTRA_NI_PIN_FORM_TYPE)?.let {
            niPinFormType = it
        }
            ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_TYPE} is missing")
    }

    private fun initializeUI() {
        findViewById<CustomBackNavigationView>(R.id.customBackNavigationView)?.let {
            it.setOnBackButtonClickListener {
                this.finish()
            }
        }

        // add BlankActivity(containing ViewPin) to the UI
//        val viewPinFragment = ViewPinFragmentFromActivity.newInstance(niInput, niPinFormType)
//        supportFragmentManager.beginTransaction().apply {
//            add(R.id.view_pin_container, viewPinFragment, BlankFragment.TAG)
//            commit()
//        }
    }

//    override fun onViewPinFragmentCompletion(response: SuccessErrorResponse) {
//        response.isSuccess?.let {
//            Log.d(MainActivity.TAG, "BlankActivity ${it.message}")
//        }
//
//        response.isError?.let {
//            Log.d(MainActivity.TAG, "BlankActivity ${it.error} ${it.errorMessage}")
//        }
//    }


}
package ae.network.nicardmanagementsdk.presentation.ui.card_details

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.databinding.ActivityCardDetailsBinding
import ae.network.nicardmanagementsdk.di.Injector
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseActivity
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragment
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CardDetailsActivity : BaseActivity<CardDetailsViewModel>(), CardDetailsFragment.OnFragmentInteractionListener {

    private lateinit var binding: ActivityCardDetailsBinding
    override lateinit var viewModel: CardDetailsViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setArchitectureComponents()
        initializeUI()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setArchitectureComponents() {
        val factory = Injector.getInstance(this).provideCardDetailsViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[CardDetailsViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_details)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initializeUI() {
        val cardDetailsFragment = CardDetailsFragment.newInstance(niInput)
        supportFragmentManager.beginTransaction().apply {
            add(binding.cardContainer.id, cardDetailsFragment, CardDetailsFragment.TAG)
            commit()
        }
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
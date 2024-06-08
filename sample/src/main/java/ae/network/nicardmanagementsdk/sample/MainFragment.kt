package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorCancelResponse
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragment
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentListener
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragment
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinFragmentFromFragment
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinFragmentFromFragment
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragment
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinFragmentFromFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nicardmanagementapp.R
import com.example.nicardmanagementapp.databinding.FragmentMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment(),
    CardDetailsFragmentListener,
    SetPinFragment.OnFragmentInteractionListener,
    VerifyPinFragment.OnFragmentInteractionListener,
    ChangePinFragment.OnFragmentInteractionListener {

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
        val cardDetailsFragment = CardDetailsFragment.newInstance(niInput, elementsColor = R.color.black_material)
        childFragmentManager.beginTransaction().apply {
            add(R.id.fragment_card_container, cardDetailsFragment, CardDetailsFragment.TAG)
            commit()
        }

        binding.setPinButton.setOnClickListener {
            val dialog = SetPinFragmentFromFragment.newInstance(niInput, niPinFormType)
            dialog.show(childFragmentManager, SetPinFragmentFromFragment.TAG)
        }

        binding.verifyPinButton.setOnClickListener {
            val dialog = VerifyPinFragmentFromFragment.newInstance(niInput, niPinFormType)
            dialog.show(childFragmentManager, VerifyPinFragmentFromFragment.TAG)
        }

        binding.changePinButton.setOnClickListener {
            val dialog = ChangePinFragmentFromFragment.newInstance(niInput, niPinFormType)
            dialog.show(childFragmentManager, ChangePinFragmentFromFragment.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCardDetailsFragmentCompletion(response: SuccessErrorResponse) {
        response.isSuccess?.let {
            Log.d(MainActivity.TAG, "MainFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(MainActivity.TAG, "MainFragment ${it.error} ${it.errorMessage}")
        }
    }

    override fun onSetPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(MainActivity.TAG, "SetPinFragmentFromFragment ${it.message}")
            lifecycleScope.launch {
                delay(500)
                startActivity(Intent(requireContext(), BlankActivity::class.java))
            }
        }

        response.isError?.let {
            Log.d(MainActivity.TAG, "SetPinFragmentFromFragment ${it.error}  ${it.errorMessage}")
            lifecycleScope.launch {
                delay(500)
                startActivity(Intent(requireContext(), BlankActivity::class.java))
            }
        }
    }

    override fun onVerifyPinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(MainActivity.TAG, "VerifyPinFragmentFromFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(MainActivity.TAG, "VerifyPinFragmentFromFragment ${it.error}  ${it.errorMessage}")
        }
    }

    override fun onChangePinFragmentCompletion(response: SuccessErrorCancelResponse) {
        response.isSuccess?.let {
            Log.d(MainActivity.TAG, "ChangePinFragmentFromFragment ${it.message}")
        }

        response.isError?.let {
            Log.d(MainActivity.TAG, "ChangePinFragmentFromFragment ${it.error}  ${it.errorMessage}")
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
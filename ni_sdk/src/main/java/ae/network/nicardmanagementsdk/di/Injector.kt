package ae.network.nicardmanagementsdk.di

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.core.ChangePinCoreComponent
import ae.network.nicardmanagementsdk.core.GetCardDetailsCoreComponent
import ae.network.nicardmanagementsdk.core.SetPinCoreComponent
import ae.network.nicardmanagementsdk.presentation.ui.card_details.CardDetailsViewModel
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinViewModel
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModel
import ae.network.nicardmanagementsdk.network.utils.ConnectionLiveData
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentViewModel
import android.content.Context

class Injector private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: Injector? = null

        fun getInstance(context: Context): Injector {
            return instance ?: synchronized(this) {
                instance ?: Injector(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    private val connectionLiveData = ConnectionLiveData(context)

    fun provideCardDetailsViewModelFactory(): ViewModelFactory<CardDetailsViewModel> {
        return ViewModelFactory {
            CardDetailsViewModel(connectionLiveData)
        }
    }

    fun provideCardDetailsFragmentViewModelFactory(niInput : NIInput): ViewModelFactory<CardDetailsFragmentViewModel> {
        return ViewModelFactory {
            val getCardDetailsCoreComponent = GetCardDetailsCoreComponent.fromFactory(niInput)
            CardDetailsFragmentViewModel(getCardDetailsCoreComponent, connectionLiveData)
        }
    }

    fun provideSetPinViewModelFactory(niInput : NIInput): ViewModelFactory<SetPinViewModel> {
        return ViewModelFactory {
            val setPinCoreComponent = SetPinCoreComponent.fromFactory(niInput)
            SetPinViewModel(setPinCoreComponent, connectionLiveData)
        }
    }

    fun provideChangePinViewModelFactory(niInput : NIInput): ViewModelFactory<ChangePinViewModel> {
        return ViewModelFactory {
            val changePinCoreComponent = ChangePinCoreComponent.fromFactory(niInput)
            ChangePinViewModel(changePinCoreComponent, connectionLiveData)
        }
    }
}
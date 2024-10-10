package ae.network.nicardmanagementsdk.di

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.core.*
import ae.network.nicardmanagementsdk.presentation.ui.card_details.CardDetailsViewModel
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentViewModel
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.ViewPinFragmentViewModel
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinViewModel
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModel
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinViewModel
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.activity.ViewPinViewModel
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

    fun provideCardDetailsViewModelFactory(): ViewModelFactory<CardDetailsViewModel> {
        return ViewModelFactory {
            CardDetailsViewModel()
        }
    }

    fun provideCardDetailsFragmentViewModelFactory(niInput : NIInput): ViewModelFactory<CardDetailsFragmentViewModel> {
        return ViewModelFactory {
            val getCardDetailsCoreComponent = GetCardDetailsCoreComponent.fromFactory(niInput)
            CardDetailsFragmentViewModel(getCardDetailsCoreComponent)
        }
    }

    fun provideSetPinViewModelFactory(niInput : NIInput): ViewModelFactory<SetPinViewModel> {
        return ViewModelFactory {
            val setPinCoreComponent = SetPinCoreComponent.fromFactory(niInput)
            SetPinViewModel(setPinCoreComponent, niInput)
        }
    }

    fun provideVerifyPinViewModelFactory(niInput : NIInput): ViewModelFactory<VerifyPinViewModel> {
        return ViewModelFactory {
            val verifyPinCoreComponent = VerifyPinCoreComponent.fromFactory(niInput)
            VerifyPinViewModel(verifyPinCoreComponent, niInput)
        }
    }

    fun provideChangePinViewModelFactory(niInput : NIInput): ViewModelFactory<ChangePinViewModel> {
        return ViewModelFactory {
            val changePinCoreComponent = ChangePinCoreComponent.fromFactory(niInput)
            ChangePinViewModel(changePinCoreComponent, niInput)
        }
    }

    fun provideViewPinFragmentViewModelFactory(niInput: NIInput): ViewModelFactory<ViewPinFragmentViewModel> {
        return ViewModelFactory {
            val viewPinComponent = ViewPinCoreComponent.fromFactory(niInput)
            ViewPinFragmentViewModel(viewPinComponent)
        }
    }

    fun provideViewPinViewModelFactory(): ViewModelFactory<ViewPinViewModel> {
        return ViewModelFactory {
            ViewPinViewModel()
        }
    }
}
package ae.network.nicardmanagementsdk.di

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
import ae.network.nicardmanagementsdk.presentation.ui.card_details.CardDetailsViewModel
import ae.network.nicardmanagementsdk.presentation.ui.card_details.fragment.CardDetailsFragmentViewModel
import ae.network.nicardmanagementsdk.presentation.ui.change_pin.ChangePinViewModel
import ae.network.nicardmanagementsdk.presentation.ui.set_pin.SetPinViewModel
import ae.network.nicardmanagementsdk.presentation.ui.verify_pin.VerifyPinViewModel
import ae.network.nicardmanagementsdk.presentation.ui.view_pin.ViewPinFragmentViewModel
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

    fun provideCardDetailsFragmentViewModelFactory(niInput: NIInput): ViewModelFactory<CardDetailsFragmentViewModel> {
        return ViewModelFactory {
            CardDetailsFragmentViewModel(niInput)
        }
    }

    fun provideSetPinViewModelFactory(
        niInput: NIInput,
        navTitleText: UIElementText,
        screenTitleText: UIElementText,
        secondStepTitleText: UIElementText,
        notMatchTitleText: UIElementText,
    ): ViewModelFactory<SetPinViewModel> {
        return ViewModelFactory {
            SetPinViewModel(niInput, navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText)
        }
    }

    fun provideVerifyPinViewModelFactory(
        niInput: NIInput,
        navTitleText: UIElementText,
        screenTitleText: UIElementText,
        secondStepTitleText: UIElementText,
        notMatchTitleText: UIElementText,
    ): ViewModelFactory<VerifyPinViewModel> {
        return ViewModelFactory {
            VerifyPinViewModel(niInput, navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText)
        }
    }

    fun provideChangePinViewModelFactory(
        niInput: NIInput,
        navTitleText: UIElementText,
        screenTitleText: UIElementText,
        newPinTitleText: UIElementText,
        approvePinTitleText: UIElementText,
        notMatchTitleText: UIElementText,
    ): ViewModelFactory<ChangePinViewModel> {
        return ViewModelFactory {
            ChangePinViewModel(niInput, navTitleText, screenTitleText, newPinTitleText, approvePinTitleText, notMatchTitleText)
        }
    }

    fun provideViewPinFragmentViewModelFactory(niInput: NIInput, timerStringTemplate: UIElementText): ViewModelFactory<ViewPinFragmentViewModel> {
        return ViewModelFactory {
            ViewPinFragmentViewModel(niInput, timerStringTemplate)
        }
    }

    fun provideViewPinViewModelFactory(): ViewModelFactory<ViewPinViewModel> {
        return ViewModelFactory {
            ViewPinViewModel()
        }
    }
}

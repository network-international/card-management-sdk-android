package ae.network.nicardmanagementsdk.di

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.UIElementText
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

    fun provideSetPinViewModelFactory(
        niInput : NIInput,
        navTitleText: UIElementText, // = UIElementText.Int(R.string.set_pin_title_en)
        screenTitleText: UIElementText, // = UIElementText.Int(R.string.set_pin_description_enter_pin_en)
        secondStepTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
        notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en
    ): ViewModelFactory<SetPinViewModel> {
        return ViewModelFactory {
            val setPinCoreComponent = SetPinCoreComponent.fromFactory(niInput)
            SetPinViewModel(setPinCoreComponent, navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText)
        }
    }

    fun provideVerifyPinViewModelFactory(
        niInput : NIInput,
        navTitleText: UIElementText, // = UIElementText.Int(R.string.verify_pin_title_en)
        screenTitleText: UIElementText, // = UIElementText.Int(R.string.verify_pin_description_en)
        secondStepTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
        notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en
    ): ViewModelFactory<VerifyPinViewModel> {
        return ViewModelFactory {
            val verifyPinCoreComponent = VerifyPinCoreComponent.fromFactory(niInput)
            VerifyPinViewModel(verifyPinCoreComponent, navTitleText, screenTitleText, secondStepTitleText, notMatchTitleText)
        }
    }

    fun provideChangePinViewModelFactory(
        niInput : NIInput,
        navTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_title_en)
        screenTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_description_enter_current_pin_en)
        newPinTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_description_enter_new_pin_en))
        approvePinTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
        notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en
    ): ViewModelFactory<ChangePinViewModel> {
        return ViewModelFactory {
            val changePinCoreComponent = ChangePinCoreComponent.fromFactory(niInput)
            ChangePinViewModel(changePinCoreComponent, navTitleText, screenTitleText, newPinTitleText, approvePinTitleText, notMatchTitleText)
        }
    }

    fun provideViewPinFragmentViewModelFactory(niInput: NIInput, timerStringTemplate: UIElementText): ViewModelFactory<ViewPinFragmentViewModel> {
        return ViewModelFactory {
            val viewPinComponent = ViewPinCoreComponent.fromFactory(niInput)
            ViewPinFragmentViewModel(viewPinComponent, timerStringTemplate)
        }
    }

    fun provideViewPinViewModelFactory(): ViewModelFactory<ViewPinViewModel> {
        return ViewModelFactory {
            ViewPinViewModel()
        }
    }
}
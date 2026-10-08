package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.usecases.implementation.SetVerifyPinUseCases
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ISetVerifyPinUseCases
import ae.network.nicardmanagementsdk.network.Network
import ae.network.nicardmanagementsdk.repository.implementation.SetPinRepository

class SetPinCoreComponent(
    setVerifyPinUseCase: ISetVerifyPinUseCases,
    input: NIInput
) : SetVerifyPinCoreBaseComponent(setVerifyPinUseCase, input) {

    companion object {
        fun fromFactory(input: NIInput): SetPinCoreComponent {
            val network = Network(input.connectionProperties)
            val setPinRepository = SetPinRepository(network.setPinApi)
            val setPinUseCases = SetVerifyPinUseCases(setPinRepository)
            return SetPinCoreComponent(setPinUseCases, input)
        }
    }
}
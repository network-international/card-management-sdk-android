package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.usecases.implementation.SetVerifyPinUseCases
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ISetVerifyPinUseCases
import ae.network.nicardmanagementsdk.network.Network
import ae.network.nicardmanagementsdk.repository.implementation.VerifyPinRepository

class VerifyPinCoreComponent(
    setVerifyPinUseCase: ISetVerifyPinUseCases,
    input: NIInput
) : SetVerifyPinCoreBaseComponent(setVerifyPinUseCase, input) {

    companion object {
        fun fromFactory(input: NIInput): VerifyPinCoreComponent {
            val network = Network(input.connectionProperties)
            val verifyPinRepository = VerifyPinRepository(network.verifyPinApi)
            val verifyPinUseCases = SetVerifyPinUseCases(verifyPinRepository)
            return VerifyPinCoreComponent(verifyPinUseCases, input)
        }
    }
}
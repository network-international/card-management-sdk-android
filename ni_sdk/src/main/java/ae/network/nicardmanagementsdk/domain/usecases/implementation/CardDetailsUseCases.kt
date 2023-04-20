package ae.network.nicardmanagementsdk.domain.usecases.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.CardDetailsResponse
import ae.network.nicardmanagementsdk.api.models.output.NICardDetailsResponse
import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ICardDetailsUseCases
import ae.network.nicardmanagementsdk.repository.interfaces.ICardDetailsRepository
import java.security.KeyPair

class CardDetailsUseCases(
    private val cardDetailsRepository: ICardDetailsRepository
    ) : BaseUseCases(), ICardDetailsUseCases {

    // The domain layer contains all the use cases of your application.
    // You can consider the use cases as class responsible of containing the business logic
    // of your application : i.e. encrypt data and make several calls to the backend
    // in order to get the final result

    override suspend fun getSecuredCardDetails(input: NIInput): NICardDetailsResponse {

        // generate RSA KeyPair
        val keyPair = CryptoManager.generateRsaKeyPair(CryptoManager.KEY_LENGTH_BITS_4K)

        // generate the publicKey
        val publicKey =  getCertificateBase64String(keyPair)

        // call API to get card details
        val cardDetailsResponse = cardDetailsRepository.getSecuredCardDetails(input, publicKey)

        // decode to clear PAN and clear CVV
        return decodeToNICardDetailsResponse(cardDetailsResponse, keyPair)
    }

    private fun decodeToNICardDetailsResponse(response: CardDetailsResponse, keyPair: KeyPair): NICardDetailsResponse {

        val clearPan = CryptoManager.rsaDecryptData(
            response.encryptedPan,
            keyPair.private,
            CryptoManager.HEX_CHUNK_BLOCK_LENGTH_4
        )

        val clearCvv = CryptoManager.rsaDecryptData(
            response.encryptedCvv2,
            keyPair.private,
            CryptoManager.HEX_CHUNK_BLOCK_LENGTH_2
        )

        return NICardDetailsResponse(
            clearPan,
            response.maskedPan,
            response.expiry,
            clearCvv,
            response.clearCardholderName
        )
    }

}

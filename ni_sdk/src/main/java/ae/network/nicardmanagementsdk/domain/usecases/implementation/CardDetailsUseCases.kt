package ae.network.nicardmanagementsdk.domain.usecases.implementation

import ae.network.nicardmanagementsdk.BuildConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.CardDetailsResponse
import ae.network.nicardmanagementsdk.api.models.output.NICardDetailsResponse
import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.core.security.SelfSignedCertificate
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ICardDetailsUseCases
import ae.network.nicardmanagementsdk.network.dto.card_details.X509CertificateBodyDto
import ae.network.nicardmanagementsdk.repository.implementation.CardDetailsRepository
import java.security.KeyPair

class CardDetailsUseCases(private val cardDetailsRepository: CardDetailsRepository) :
    ICardDetailsUseCases {

    // The domain layer contains all the use cases of your application.
    // You can consider the use cases as class responsible of containing the business logic
    // of your application.
    // i.e. encrypt data and make several calls to the backend in order to get the final result

    override suspend fun getSecuredCardDetails(input: NIInput): NICardDetailsResponse {
        val keyPair = CryptoManager.generateRsaKeyPair(CryptoManager.KEY_LENGTH_BITS_4K)
        val cardDetailsResponse = cardDetailsRepository.getSecuredCardDetails(
            input.connectionProperties.token,
            input.cardIdentifierId,
            input.cardIdentifierType,
            getX509CertificateDto(keyPair)
        )

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

    private fun getX509CertificateDto(keyPair: KeyPair): X509CertificateBodyDto {
        val certificate = SelfSignedCertificate(
            fqdn = BuildConfig.LIBRARY_PACKAGE_NAME,
            keyPair = keyPair
        )
        return X509CertificateBodyDto(certificate.certificateBase64String)
    }

}

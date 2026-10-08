package ae.network.nicardmanagementsdk.domain.usecases.implementation

import ae.network.nicardmanagementsdk.BuildConfig
import ae.network.nicardmanagementsdk.core.security.SelfSignedCertificate
import java.security.KeyPair

abstract class BaseUseCases {

    fun getCertificateBase64String(keyPair: KeyPair): String {
        val certificate = SelfSignedCertificate(
            fqdn = BuildConfig.LIBRARY_PACKAGE_NAME,
            keyPair = keyPair
        )
        return certificate.certificateBase64String
    }
}
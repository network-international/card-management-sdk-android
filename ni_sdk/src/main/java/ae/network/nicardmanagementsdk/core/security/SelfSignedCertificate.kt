package ae.network.nicardmanagementsdk.core.security

import android.util.Base64
import android.util.Log
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.Time
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*


/**
 * Creates a new instance.
 *
 * @param fqdn           The fully qualified domain name
 * @param secureRandom   The [java.security.SecureRandom] to use
 * @param keyPair        The pair of keys generated with RSA
 * @param notBefore      Certificate is not valid before this time
 * @param notAfter       Certificate is not valid after this time
 */
class SelfSignedCertificate(
    fqdn: String = DEFAULT_FQDN,
    secureRandom: SecureRandom = SecureRandom(),
    keyPair: KeyPair,
    notBefore: Date? = DEFAULT_NOT_BEFORE,
    notAfter: Date? = DEFAULT_NOT_AFTER
) {
    /**
     * Returns the generated X.509 certificate.
     */
    val certificateBase64String: String
        get() = base64StringCertificate

    private val base64StringCertificate: String

    init {
        base64StringCertificate = try {
            // Try Bouncy Castle if the current framework didn't support sun.security.x509.
            buildCertificate(fqdn, secureRandom, keyPair, notBefore, notAfter)
        } catch (t2: Throwable) {
            Log.d(
                TAG,
                "Failed to generate a self-signed X.509 certificate using Bouncy Castle:",
                t2
            )
            throw CertificateException(
                "No provider succeeded to generate a self-signed certificate. See debug log for the root cause.",
                t2
            )
        }
    }

    private fun getEncodedBase64String(certificate: X509Certificate): String {
        return Base64.encodeToString(certificate.encoded, Base64.NO_WRAP)
    }

    @Throws(Exception::class)
    private fun buildCertificate(fqdn: String, random: SecureRandom, keyPair: KeyPair, notBefore: Date?, notAfter: Date?): String {
        // Prepare the information required for generating an X.509 certificate.
        val certificateOwner = X500Name("CN=$fqdn")
        val locale = Locale.forLanguageTag("en-US")
        val certificateBuilder: X509v3CertificateBuilder = JcaX509v3CertificateBuilder(
            certificateOwner, BigInteger(64, random), Time(notBefore, locale), Time(notAfter, locale), certificateOwner, keyPair.public)
        val contentSigner: ContentSigner = JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keyPair.private)
        val certificateHolder = certificateBuilder.build(contentSigner)
        val certificate: X509Certificate = JcaX509CertificateConverter().setProvider(provider).getCertificate(certificateHolder)

        certificate.verify(keyPair.public)
        return getEncodedBase64String(certificate)
    }

    companion object {
        private val TAG = SelfSignedCertificate::class.java.simpleName

        private const val ONE_DAY_MILLIS = 86400000L

        /**
         * Current time minus 1 day, just in case software clock goes back due to time synchronization
         */
        private val DEFAULT_NOT_BEFORE: Date = Date(System.currentTimeMillis() - ONE_DAY_MILLIS)

        /**
         * Current time plus 1 day, as the certificate will be built each time, the lifespan may be short like this
         */
        private val DEFAULT_NOT_AFTER: Date = Date(System.currentTimeMillis() + ONE_DAY_MILLIS)



        /**
         * FQDN to use if none is specified.
         */
        private const val DEFAULT_FQDN = "example.com"

        private val provider: Provider = BouncyCastleProvider()
    }
}
package data.repositories

import com.webauthn4j.WebAuthnManager
import com.webauthn4j.credential.CredentialRecord
import com.webauthn4j.credential.CredentialRecordImpl
import com.webauthn4j.data.AuthenticationParameters
import com.webauthn4j.data.RegistrationParameters
import com.webauthn4j.data.RegistrationRequest
import com.webauthn4j.data.client.Origin
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.server.ServerProperty
import com.webauthn4j.util.Base64UrlUtil
import config.SecurityConfig
import data.datasources.AuthDataSource
import data.dtos.login.CompleteLoginRequestDTO
import data.dtos.login.StartLoginResponseDTO
import data.dtos.registration.AuthenticatorSelection
import data.dtos.registration.CompleteRegistrationRequestDTO
import data.dtos.registration.PublicKeyCredentialParams
import data.dtos.registration.Rp
import data.dtos.registration.StartRegistrationResponseDTO
import data.dtos.registration.UserRegistration
import domain.models.User
import domain.repositories.AuthRepository
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val securityConfig: SecurityConfig
) : AuthRepository {

    companion object {
        // Server configuration
        private const val RP_ID = "passkeysauth-be-example.onrender.com"
        private const val RP_NAME = "PasskeyAuth-KMP-App-Example"
        private const val PUBLIC_KEY_TYPE = "public-key"
        private const val PUBLIC_KEY_ALG_ES_256 = -7
        private const val PUBLIC_KEY_ALG_ES_257 = -257
        private const val DEFAULT_ATTESTATION = "direct"

        private const val REGISTRATION_TIMEOUT = 20000L
        private const val LOGIN_TIMEOUT = 20000L

        private const val ANDROID_ORIGIN_PREFIX = "android:apk-key-hash:"
        // This is a debug keystore for purpose test only
        private const val ANDROID_APP_FINGERPRINT = "74:BD:83:D6:D4:0D:DB:79:62:DA:1B:6B:6B:CD:B9:48:77:8A:58:E9:19:A6:CE:45:B5:29:53:22:30:94:76:B8"
        private const val WEB_ORIGIN = "https://$RP_ID"
    }

    private val random = SecureRandom()
    private val webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager()

    private val origins: Set<Origin> by lazy {
        val androidOrigin = calculateAndroidOrigin()
        val webOrigin = Origin(WEB_ORIGIN)
        setOf(
            androidOrigin,
            webOrigin
        )
    }

    /**
     * REGISTRATION
     */

    override fun startRegistration(username: String): StartRegistrationResponseDTO {
        val user = authDataSource.findUser(username) ?: User(generateUserIdBase64(), username)
        val challenge = generateChallenge()

        authDataSource.saveRegistrationChallenge(username, challenge)
        authDataSource.saveUser(user)

        return StartRegistrationResponseDTO(
            rp = Rp(
                id = RP_ID,
                name = RP_NAME
            ),
            user = UserRegistration(
                id = user.id,
                name = user.username,
                displayName = user.username
            ),
            challenge = challenge,
            pubKeyCredParams = listOf(
                PublicKeyCredentialParams(
                    type = PUBLIC_KEY_TYPE,
                    alg = PUBLIC_KEY_ALG_ES_256
                ),
                PublicKeyCredentialParams(
                    type = PUBLIC_KEY_TYPE,
                    alg = PUBLIC_KEY_ALG_ES_257
                )
            ),
            authenticatorSelection = AuthenticatorSelection(
                userVerification = "preferred"
            ),
            timeout = REGISTRATION_TIMEOUT,
            attestation = DEFAULT_ATTESTATION
        )
    }

    override fun completeRegistration(username: String, request: CompleteRegistrationRequestDTO) {
        val challenge = authDataSource.getRegistrationChallenge(username)
            ?: throw IllegalArgumentException("No registration in progress")

        val serverProperty = ServerProperty(
            origins,
            RP_ID,
            DefaultChallenge(Base64UrlUtil.decode(challenge)),
            null
        )

        val parsedRegistrationRequest = webAuthnManager.parse(
            RegistrationRequest(
                Base64UrlUtil.decode(request.attestationObject),
                Base64UrlUtil.decode(request.clientDataJSON),
                emptySet()
            )
        )

        val parsedOrigin = parsedRegistrationRequest.collectedClientData?.origin
        if (!origins.contains(parsedOrigin)) {
            throw Exception("Missing origin: $parsedOrigin")
        }

        val registrationVerificationResult = webAuthnManager.verify(
            parsedRegistrationRequest,
            RegistrationParameters(
                serverProperty,
                null,
                true
            )
        )

        val credential: CredentialRecord = registrationVerificationResult.attestationObject?.let {
            CredentialRecordImpl(
                it,
                registrationVerificationResult.collectedClientData,
                registrationVerificationResult.clientExtensions,
                registrationVerificationResult.transports
            )
        } ?: throw Exception("No attested credential data found")

        authDataSource.findUser(username)?.credentials?.add(credential)
    }

    /**
     * LOGIN
     */

    override fun startLogin(username: String): StartLoginResponseDTO {
        if (authDataSource.findUser(username) == null) {
            throw IllegalArgumentException("User not found")
        }
        val challenge = generateChallenge()

        authDataSource.saveAuthenticationChallenge(username, challenge)

        return StartLoginResponseDTO(
            challenge = challenge,
            allowCredentials = emptyList(),
            timeout = LOGIN_TIMEOUT,
            userVerification = "required",
            rpId = RP_ID
        )
    }

    override fun completeLogin(username: String, request: CompleteLoginRequestDTO): String {
        val challenge = authDataSource.getAuthenticationChallenge(username)
            ?: throw IllegalArgumentException("No authentication in progress")

        val user = authDataSource.findUser(username) ?: throw IllegalArgumentException("User not found")
        val credential = user.credentials.find {
            Base64UrlUtil.encodeToString(it.attestedCredentialData.credentialId) == request.credentialId
        } ?: throw IllegalArgumentException("Unknown credential")

        val serverProperty = ServerProperty(
            origins,
            RP_ID,
            DefaultChallenge(Base64UrlUtil.decode(challenge)),
            null
        )

        val parsedAuthenticationRequest = webAuthnManager.parse(
            com.webauthn4j.data.AuthenticationRequest(
                Base64UrlUtil.decode(request.credentialId),
                request.userHandle?.let { Base64UrlUtil.decode(it) },
                Base64UrlUtil.decode(request.authenticatorData),
                Base64UrlUtil.decode(request.clientDataJSON),
                Base64UrlUtil.decode(request.signature)
            )
        )

        webAuthnManager.verify(
            parsedAuthenticationRequest,
            AuthenticationParameters(
                serverProperty,
                credential,
                null,
                true
            )
        )

        return securityConfig.createToken(user.id)
    }

    override fun findUserById(id: String): User? =
        authDataSource.findUserById(id)

    private fun generateUserIdBase64(): String {
        val uuid = UUID.randomUUID()
        val byteBuffer = ByteBuffer.allocate(16)
        byteBuffer.putLong(uuid.mostSignificantBits)
        byteBuffer.putLong(uuid.leastSignificantBits)
        val uuidBytes = byteBuffer.array()
        return String(Base64.getUrlEncoder().encode(uuidBytes))
    }

    private fun generateChallenge(): String {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return String(Base64.getUrlEncoder().encode(bytes))
    }

    private fun calculateAndroidOrigin(): Origin {
        val fingerprintBytes = ANDROID_APP_FINGERPRINT.replace(":", "").chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()

        val base64Encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(fingerprintBytes)

        return Origin("$ANDROID_ORIGIN_PREFIX$base64Encoded")
    }

}
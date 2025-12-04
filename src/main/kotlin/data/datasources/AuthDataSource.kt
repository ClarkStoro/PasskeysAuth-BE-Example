package data.datasources

import com.webauthn4j.util.Base64UrlUtil
import domain.models.User
import java.util.concurrent.ConcurrentHashMap

data class ChallengeData(
    val challenge: String,
    val expiresAt: Long
)

interface AuthDataSource {
    fun saveUser(user: User)
    fun findUser(username: String): User?
    fun findUserById(id: String): User?
    fun findUserByCredentialId(credentialId: String): User?
    fun saveRegistrationChallenge(username: String, challenge: String)
    fun getRegistrationChallenge(username: String): String?
    fun saveAuthenticationChallenge(key: String, challenge: String)
    fun getAuthenticationChallenge(key: String): String?
}

class AuthDataSourceImpl : AuthDataSource {
    private val users = ConcurrentHashMap<String, User>()
    private val registrationChallenges = ConcurrentHashMap<String, String>()
    private val authenticationChallenges = ConcurrentHashMap<String, ChallengeData>()

    companion object {
        private const val CHALLENGE_TIMEOUT_MS = 300000L // 5 minutes
    }

    override fun saveUser(user: User) {
        users[user.username] = user
    }

    override fun findUser(username: String): User? = users[username]

    override fun findUserById(id: String): User? {
        return users.values.find { it.id == id }
    }

    override fun findUserByCredentialId(credentialId: String): User? {
        return users.values.find { user ->
            user.credentials.any { credential ->
                Base64UrlUtil.encodeToString(credential.attestedCredentialData.credentialId) == credentialId
            }
        }
    }

    override fun saveRegistrationChallenge(username: String, challenge: String) {
        registrationChallenges[username] = challenge
    }

    override fun getRegistrationChallenge(username: String): String? =
        registrationChallenges.remove(username)

    override fun saveAuthenticationChallenge(key: String, challenge: String) {
        val expiresAt = System.currentTimeMillis() + CHALLENGE_TIMEOUT_MS
        authenticationChallenges[key] = ChallengeData(challenge, expiresAt)
    }

    override fun getAuthenticationChallenge(key: String): String? {
        val challengeData = authenticationChallenges.remove(key) ?: return null

        // Check if challenge has expired
        if (System.currentTimeMillis() > challengeData.expiresAt) {
            return null
        }

        return challengeData.challenge
    }

}
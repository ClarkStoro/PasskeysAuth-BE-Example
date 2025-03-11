package data.datasources

import domain.models.User
import java.util.concurrent.ConcurrentHashMap

interface AuthDataSource {
    fun saveUser(user: User)
    fun findUser(username: String): User?
    fun findUserById(id: String): User?
    fun saveRegistrationChallenge(username: String, challenge: String)
    fun getRegistrationChallenge(username: String): String?
    fun saveAuthenticationChallenge(username: String, challenge: String)
    fun getAuthenticationChallenge(username: String): String?
}

class AuthDataSourceImpl : AuthDataSource {
    private val users = ConcurrentHashMap<String, User>()
    private val registrationChallenges = ConcurrentHashMap<String, String>()
    private val authenticationChallenges = ConcurrentHashMap<String, String>()

    override fun saveUser(user: User) {
        users[user.username] = user
    }

    override fun findUser(username: String): User? = users[username]

    override fun findUserById(id: String): User? {
        return users.values.find { it.id == id }
    }

    override fun saveRegistrationChallenge(username: String, challenge: String) {
        registrationChallenges[username] = challenge
    }

    override fun getRegistrationChallenge(username: String): String? =
        registrationChallenges.remove(username)

    override fun saveAuthenticationChallenge(username: String, challenge: String) {
        authenticationChallenges[username] = challenge
    }

    override fun getAuthenticationChallenge(username: String): String? =
        authenticationChallenges.remove(username)

}
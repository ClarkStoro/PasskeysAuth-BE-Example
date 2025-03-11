package config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import data.dtos.todos.UserPrincipalDTO
import domain.models.AppConfig
import io.ktor.server.auth.jwt.*
import java.util.*

class SecurityConfig(
    private val appConfig: AppConfig
) {

    companion object {
        private const val SECRET = "a3f1d5c7b92e4f68af72c19d3e7a8b0c5d4e3f9a6b7c8d1e2f3a4b5c6d7e8f9"
        private val algorithm = Algorithm.HMAC256(SECRET)
        private const val EXPIRATION = 3_600_000

        private const val USER_ID = "userId"
    }


    // Token creation
    fun createToken(userId: String): String {
        return JWT.create()
            .withAudience(appConfig.audience)
            .withIssuer(appConfig.issuer)
            .withClaim(USER_ID, userId)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION))
            .sign(algorithm)
    }

    // JWT Authentication configuration
    fun configureAuth(config: JWTAuthenticationProvider.Config) {
        config.apply {
            realm = appConfig.realm

            verifier(
                JWT.require(algorithm)
                .withIssuer(appConfig.issuer)
                .withAudience(appConfig.audience)
                .build()
            )

            validate { credential ->
                val userId = credential.payload.getClaim(USER_ID).asString()
                if (userId != null && credential.payload.audience.contains(appConfig.audience)) {
                    UserPrincipalDTO(
                        userId = userId,
                        expiresAt = credential.payload.expiresAt.time
                    )
                } else {
                    null
                }
            }
        }
    }
}
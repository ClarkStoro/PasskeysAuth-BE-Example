package config

import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val securityConfig by inject<SecurityConfig>()
    authentication {
        jwt {
            securityConfig.configureAuth(this)
        }
    }
}

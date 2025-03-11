package config

import domain.models.AppConfig
import domain.repositories.AuthRepository
import features.auth.authRoutes
import features.configuration.configurationRoutes
import features.todos.protectedExampleRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val appConfig by inject<AppConfig>()
    val authRepository by inject<AuthRepository>()

    routing {
        configurationRoutes(appConfig)
        authRoutes(authRepository)
        protectedExampleRoutes(authRepository)
    }
}

object Params {
    const val USERNAME = "username"
    const val STATUS = "status"
    const val IS_PROD = "isProd"
}

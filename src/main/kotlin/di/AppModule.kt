package di

import config.SecurityConfig
import data.datasources.AuthDataSource
import data.datasources.AuthDataSourceImpl
import data.repositories.AuthRepositoryImpl
import domain.models.AppConfig
import domain.repositories.AuthRepository
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.install
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(getEnvironmentModule(environment))
        modules(appModule)
    }
}

private val appModule = module {
    single<SecurityConfig> { SecurityConfig(get()) }
    single<AuthDataSource> { AuthDataSourceImpl() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}

private fun getEnvironmentModule(
    environment: ApplicationEnvironment
): Module {
    val env = environment.config.propertyOrNull("ktor.environment")?.getString()
    val issuer = environment.config.propertyOrNull("jwt.domain")?.getString().orEmpty()
    val audience = environment.config.propertyOrNull("jwt.audience")?.getString().orEmpty()
    val realm = environment.config.propertyOrNull("jwt.realm")?.getString().orEmpty()

    return module {
        single<AppConfig> {
            AppConfig(
                isProd = env == "prod",
                issuer = issuer,
                audience = audience,
                realm = realm
            )
        }
    }
}
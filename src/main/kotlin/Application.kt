import config.configureSerialization
import di.configureDI
import config.configureRouting
import config.configureSecurity
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureSerialization()
    configureSecurity()
    configureRouting()

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }
}

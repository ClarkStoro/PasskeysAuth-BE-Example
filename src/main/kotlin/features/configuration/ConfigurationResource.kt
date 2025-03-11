package features.configuration

import domain.models.AppConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import config.Params
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import java.io.File

fun Route.configurationRoutes(appConfig: AppConfig) {

    get("/checkStatus") {
        call.respond(
            mapOf(
                Params.STATUS to "OK",
                Params.IS_PROD to appConfig.isProd.toString()
            )
        )
    }

    get("/.well-known/assetlinks.json") {
        val inputStream = this::class.java.getResourceAsStream("/static/.well-known/assetlinks.json")
        if (inputStream != null) {
            val tempFile = File.createTempFile("assetlinks", ".json")
            tempFile.deleteOnExit()
            tempFile.writeBytes(inputStream.readBytes())
            call.respondText(tempFile.readText(), ContentType.Application.Json)
        } else {
            call.respond(HttpStatusCode.Forbidden)
        }
    }

    get("/.well-known/apple-app-site-association") {
        val inputStream = this::class.java.getResourceAsStream("/static/.well-known/apple-app-site-association")
        if (inputStream != null) {
            val tempFile = File.createTempFile("apple-app-site-association", "")
            tempFile.deleteOnExit()
            tempFile.writeBytes(inputStream.readBytes())
            call.respondText(tempFile.readText(), ContentType.Application.Json)
        } else {
            call.respond(HttpStatusCode.Forbidden)
        }
    }
}
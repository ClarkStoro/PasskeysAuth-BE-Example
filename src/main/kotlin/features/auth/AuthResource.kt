package features.auth

import config.Params
import data.dtos.login.CompleteLoginRequestDTO
import data.dtos.login.CompleteLoginResponseDTO
import data.dtos.registration.CompleteRegistrationRequestDTO
import data.dtos.registration.CompleteRegistrationResponseDTO
import domain.repositories.AuthRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import utils.getDefaultError

fun Route.authRoutes(authRepository: AuthRepository) {

    route("/auth") {
        post("/register/start") {
            try {
                val username = call.request.queryParameters[Params.USERNAME]
                    ?: throw BadRequestException("Missing username")
                val response = authRepository.startRegistration(username)
                call.respond(response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }

        post("/register/complete") {
            try {
                val username = call.request.queryParameters[Params.USERNAME]
                    ?: throw BadRequestException("Missing username")
                val request = call.receive<CompleteRegistrationRequestDTO>()

                authRepository.completeRegistration(username, request)
                call.respond(CompleteRegistrationResponseDTO(message = "User $username registered successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }

        post("/login/start") {
            try {
                val username = call.request.queryParameters[Params.USERNAME]
                    ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                val response = authRepository.startLogin(username)
                call.respond(response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }

        post("/login/complete") {
            try {
                val username = call.request.queryParameters[Params.USERNAME]
                    ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                val request = call.receive<CompleteLoginRequestDTO>()
                val resultToken = authRepository.completeLogin(username, request)
                call.respond(
                    CompleteLoginResponseDTO(
                        message = "Welcome back User $username login successfully!",
                        token = resultToken
                    )
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }
    }
}
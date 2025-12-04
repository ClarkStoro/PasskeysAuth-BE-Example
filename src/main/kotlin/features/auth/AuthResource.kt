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
import org.slf4j.LoggerFactory
import utils.getDefaultError

fun Route.authRoutes(authRepository: AuthRepository) {
    val logger = LoggerFactory.getLogger("AuthResource")

    route("/auth") {
        post("/register/start") {
            try {
                logger.info("POST /auth/register/start - Starting registration")
                val username = call.request.queryParameters[Params.USERNAME]
                    ?: throw BadRequestException("Missing username")
                val response = authRepository.startRegistration(username)
                logger.info("POST /auth/register/start - Registration challenge created")
                call.respond(response)
            } catch (e: Exception) {
                logger.error("POST /auth/register/start - Error: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }

        post("/register/complete") {
            try {
                logger.info("POST /auth/register/complete - Completing registration")
                val username = call.request.queryParameters[Params.USERNAME]
                    ?: throw BadRequestException("Missing username")
                val request = call.receive<CompleteRegistrationRequestDTO>()

                authRepository.completeRegistration(username, request)
                logger.info("POST /auth/register/complete - Registration completed successfully")
                call.respond(CompleteRegistrationResponseDTO(message = "User $username registered successfully!"))
            } catch (e: Exception) {
                logger.error("POST /auth/register/complete - Error: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }

        post("/login/start") {
            try {
                logger.info("POST /auth/login/start - Starting usernameless login")
                val response = authRepository.startLogin()
                logger.info("POST /auth/login/start - Login challenge created")
                call.respond(response)
            } catch (e: Exception) {
                logger.error("POST /auth/login/start - Error: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }

        post("/login/complete") {
            try {
                logger.info("POST /auth/login/complete - Attempting login completion")
                val sessionId = call.request.queryParameters[Params.SESSION_ID]
                    ?: throw BadRequestException("Missing sessionId")
                val request = call.receive<CompleteLoginRequestDTO>()
                val resultToken = authRepository.completeLogin(sessionId, request)
                logger.info("POST /auth/login/complete - Login successful")
                call.respond(
                    CompleteLoginResponseDTO(
                        message = "Welcome back! Login successful!",
                        token = resultToken
                    )
                )
            } catch (e: Exception) {
                logger.error("POST /auth/login/complete - Error: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, e.getDefaultError())
            }
        }
    }
}
package domain.repositories

import data.dtos.login.CompleteLoginRequestDTO
import data.dtos.login.StartLoginResponseDTO
import data.dtos.registration.CompleteRegistrationRequestDTO
import data.dtos.registration.StartRegistrationResponseDTO
import domain.models.User

interface AuthRepository {

    // Registration
    fun startRegistration(username: String): StartRegistrationResponseDTO
    fun completeRegistration(username: String, request: CompleteRegistrationRequestDTO)

    // Login
    fun startLogin(): StartLoginResponseDTO
    fun completeLogin(sessionId: String, request: CompleteLoginRequestDTO): String

    fun findUserById(id: String): User?
}
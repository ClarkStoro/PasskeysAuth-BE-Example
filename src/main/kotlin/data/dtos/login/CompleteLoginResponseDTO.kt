package data.dtos.login

import kotlinx.serialization.Serializable

@Serializable
data class CompleteLoginResponseDTO(
    val token: String,
    val message: String
)
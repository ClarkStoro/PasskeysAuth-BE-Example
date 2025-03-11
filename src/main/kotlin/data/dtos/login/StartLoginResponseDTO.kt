package data.dtos.login

import kotlinx.serialization.Serializable

@Serializable
data class StartLoginResponseDTO(
    val challenge: String,
    val allowCredentials: List<AllowCredentialDTO>,
    val timeout: Long,
    val userVerification: String,
    val rpId: String
)
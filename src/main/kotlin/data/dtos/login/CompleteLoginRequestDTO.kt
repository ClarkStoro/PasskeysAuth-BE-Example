package data.dtos.login

import kotlinx.serialization.Serializable

@Serializable
data class CompleteLoginRequestDTO(
    val credentialId: String,
    val clientDataJSON: String,
    val authenticatorData: String,
    val signature: String,
    val userHandle: String? = null
)
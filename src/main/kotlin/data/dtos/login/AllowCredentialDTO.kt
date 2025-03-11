package data.dtos.login

import kotlinx.serialization.Serializable

@Serializable
data class AllowCredentialDTO(
    val type: String = "public-key",
    val id: String
)
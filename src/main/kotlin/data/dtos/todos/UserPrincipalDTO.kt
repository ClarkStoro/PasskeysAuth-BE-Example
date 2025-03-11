package data.dtos.todos

import kotlinx.serialization.Serializable

@Serializable
data class UserPrincipalDTO(
    val userId: String,
    val expiresAt: Long
)
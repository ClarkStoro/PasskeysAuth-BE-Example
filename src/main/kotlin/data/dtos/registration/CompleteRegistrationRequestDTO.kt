package data.dtos.registration

import kotlinx.serialization.Serializable

@Serializable
data class CompleteRegistrationRequestDTO(
    val clientDataJSON: String,
    val attestationObject: String
)
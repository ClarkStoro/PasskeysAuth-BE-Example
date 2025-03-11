package domain.models

import com.webauthn4j.credential.CredentialRecord

data class User(
    val id: String,
    val username: String,
    val credentials: MutableList<CredentialRecord> = mutableListOf()
)
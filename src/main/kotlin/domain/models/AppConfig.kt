package domain.models

data class AppConfig (
    val isProd: Boolean,
    val issuer: String,
    val audience: String,
    val realm: String
)
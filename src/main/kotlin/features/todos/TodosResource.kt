package features.todos

import data.dtos.todos.UserPrincipalDTO
import domain.repositories.AuthRepository
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import data.dtos.todos.GetTodosResponseDTO
import data.dtos.todos.TodoDTO


fun Route.protectedExampleRoutes(authRepository: AuthRepository) {
    authenticate {
        get("/todos") {
            val principal = call.principal<UserPrincipalDTO>()
            val user = authRepository.findUserById(principal?.userId.orEmpty())
            val fakeTodos = listOf(
                TodoDTO(title = "🔐 Test Passkey Login", description = "Verify authentication works smoothly", done = true),
                TodoDTO(title = "🎤 Prepare Demo Script", description = "Practice the presentation flow", done = true),
                TodoDTO(title = "📱 Test on Multiple Devices", description = "Check iOS and Android compatibility", done = false),
                TodoDTO(title = "✨ Polish UI Components", description = "Final touch-ups for Material 3 design", done = true),
                TodoDTO(title = "🚀 Deploy Backend", description = "Ensure server is running for demo", done = false),
                TodoDTO(title = "📊 Add Demo Analytics", description = "Track user interactions during presentation", done = false),
                TodoDTO(title = "🛡️ Review Security Best Practices", description = "Double-check WebAuthn implementation", done = true),
                TodoDTO(title = "🎯 Create Backup Demo Plan", description = "Prepare for potential technical issues", done = false),
                TodoDTO(title = "📝 Update Documentation", description = "Add comments for code walkthrough", done = false),
                TodoDTO(title = "🎨 Test Dark Mode Support", description = "Ensure UI works in both themes", done = false)
            )
            call.respond(
                GetTodosResponseDTO(
                    username = user?.username,
                    todos = fakeTodos
                )
            )
        }
    }
}
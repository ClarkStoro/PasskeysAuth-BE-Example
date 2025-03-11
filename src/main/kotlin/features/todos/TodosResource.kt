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
                TodoDTO(title = "Buy milk", description = "Go to the supermarket and get whole milk", done = false),
                TodoDTO(title = "Clean the desk", description = "Organize documents and throw away unnecessary papers", done = true),
                TodoDTO(title = "Call the mechanic", description = "Schedule a car service appointment", done = false),
                TodoDTO(title = "Read a chapter of the book", description = "Continue reading the novel started", done = true),
                TodoDTO(title = "Update CV", description = "Add recent projects and review the layout", done = false),
                TodoDTO(title = "Book the flight", description = "Check for deals and purchase tickets", done = false),
                TodoDTO(title = "Exercise", description = "30 minutes of bodyweight workout", done = true),
                TodoDTO(title = "Renew gym membership", description = "Check annual offers", done = false),
                TodoDTO(title = "Cook dinner", description = "Prepare pesto pasta with salad", done = true),
                TodoDTO(title = "Write a new idea for the side project", description = "Note the idea in Notion", done = false)
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
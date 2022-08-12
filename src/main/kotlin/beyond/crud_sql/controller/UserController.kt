package beyond.crud_sql.controller

import beyond.crud_sql.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(val userService: UserService) {

    fun join() {
    }
}
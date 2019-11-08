package ru.chinchillas.br.brchinchillas.users

import org.springframework.http.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = ["*"])
class UsersController (
        internal val usersRepository: UsersRepository,
        internal val tokensRepository: TokensRepository,
        internal val blockedUsersRepository: BlockedUsersRepository
) {
    @PostMapping("/registration")
    fun registration(@RequestBody newUser: User, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        return when {
            usersRepository.existsByEmail(newUser.email) ->
                ResponseEntity(mapOf<String, Any>("error" to ERROR_EMAIL_EXISTS), HttpStatus.CONFLICT)
            blockedUsersRepository.existsByEmailOrPhoneOrAddress(
                    newUser.email,
                    newUser.phone,
                    request.remoteAddr
            ) -> ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_IS_BLOCKED), HttpStatus.CONFLICT)
            else -> {
                val encoder = BCryptPasswordEncoder()
                newUser.password = encoder.encode(newUser.password)
                newUser.isCheckedEmail = false
                newUser.lastActive = System.currentTimeMillis()
                usersRepository.save(newUser)
                checkEmail(newUser)

                val token = Token(
                        newUser.id,
                        encoder.encode("BR_${newUser.id}_${request.remoteAddr}_${request.getHeader("User-Agent")}"),
                        System.currentTimeMillis()
                )
                tokensRepository.save(token)

                ResponseEntity(mapOf(
                        "id" to newUser.id,
                        "token" to token.value
                ), HttpStatus.OK)
            }
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody authorization: Authorization, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val user = usersRepository.findOneByEmail(authorization.email)
        val encoder = BCryptPasswordEncoder()
        return when {
            user == null -> ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_NOT_FOUND), HttpStatus.CONFLICT)
            !encoder.matches(authorization.password, user.password) ->
                ResponseEntity(mapOf<String, Any>("error" to ERROR_WRONG_PASSWORD), HttpStatus.CONFLICT)
            else -> {
                val token = Token(
                        user.id,
                        encoder.encode("BR_${user.id}_${request.remoteAddr}_${request.getHeader("User-Agent")}"),
                        System.currentTimeMillis()
                )
                tokensRepository.save(token)
                user.password = ""
                user.lastActive = System.currentTimeMillis()
                ResponseEntity(mapOf(
                        "data" to user,
                        "token" to token.value
                ), HttpStatus.OK)
            }
        }
    }

    @PutMapping("/update")
    fun updateUser(@RequestBody newUser: User, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val token = tokensRepository.findOneByValue(request.getHeader("Token"))
        return if(token == null) ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_NOT_FOUND), HttpStatus.CONFLICT)
        else {
            val user = usersRepository.getOne(token.userId)
            when {
                blockedUsersRepository.existsByEmailOrPhoneOrAddress(
                        user.email,
                        user.phone,
                        request.remoteAddr
                ) -> ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_IS_BLOCKED), HttpStatus.CONFLICT)
                else -> {
                    user.firstName = newUser.firstName
                    user.lastActive = newUser.lastActive
                    user.city = newUser.city
                    user.phone = newUser.phone
                    if(user.email != newUser.email) {
                        user.email = newUser.email
                        user.isCheckedEmail = false
                        checkEmail(user)
                    }
                    usersRepository.save(user)
                    ResponseEntity(HttpStatus.OK)
                }
            }
        }
    }

    @PutMapping("/password")
    fun updatePassword(@RequestBody changePassword: ChangePassword, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val token = tokensRepository.findOneByValue(request.getHeader("Token"))
        return if(token == null) ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_NOT_FOUND), HttpStatus.CONFLICT)
        else {
            val user = usersRepository.getOne(token.userId)
            val encoder = BCryptPasswordEncoder()
            when {
                blockedUsersRepository.existsByEmailOrPhoneOrAddress(
                        user.email,
                        user.phone,
                        request.remoteAddr
                ) -> ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_IS_BLOCKED), HttpStatus.CONFLICT)
                !encoder.matches(changePassword.oldPassword, user.password) ->
                    ResponseEntity(mapOf<String, Any>("error" to ERROR_WRONG_PASSWORD), HttpStatus.CONFLICT)
                else -> {
                    user.password = encoder.encode(changePassword.newPassword)
                    usersRepository.save(user)
                    ResponseEntity(HttpStatus.OK)
                }
            }
        }
    }

    @GetMapping("/confirmEmail")
    fun confirmEmail(@RequestParam("id") id: Long, @RequestParam("hash") hash: String) {
        val user = usersRepository.getOne(id)
        if(BCryptPasswordEncoder().matches("BR_check_email_$id", hash)) {
            user.isCheckedEmail = true
            usersRepository.save(user)
        }
    }

    @GetMapping("/checkToken")
    fun checkToken(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val token = tokensRepository.findOneByValue(request.getHeader("Token"))
        return when {
            token == null -> ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_NOT_FOUND), HttpStatus.CONFLICT)
            blockedUsersRepository.existsByUserId(token.userId) ->
                ResponseEntity(mapOf<String, Any>("error" to ERROR_USER_IS_BLOCKED), HttpStatus.CONFLICT)
            else -> {
                val user = usersRepository.getOne(token.userId)
                user.lastActive = System.currentTimeMillis()
                usersRepository.save(user)
                token.time = System.currentTimeMillis()
                tokensRepository.save(token)
                ResponseEntity(mapOf("status" to "ok"), HttpStatus.OK)
            }
        }
    }

    private fun checkEmail(user: User) {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(
                mapOf(
                        "email" to user.email,
                        "id" to user.id,
                        "hash" to BCryptPasswordEncoder().encode("BR_check_email_${user.id}")
                ).toString(), headers)
        restTemplate.postForObject(
                "https://br-chinchillas.ru/back/checkEmail.php",
                request,
                String::class.java
        )
    }

    companion object {
        private const val ERROR_EMAIL_EXISTS = 1
        private const val ERROR_USER_IS_BLOCKED = 2
        private const val ERROR_USER_NOT_FOUND = 3
        private const val ERROR_WRONG_PASSWORD = 4
    }
}
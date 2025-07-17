package com.github.holly.accountability.config.user

import com.github.holly.accountability.validation.BindingResultWrapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.util.MultiValueMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/registration")
@ResponseBody
class RegistrationController(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    @PostMapping("")
    fun registerUser(@Valid @RequestBody registerUser: RegisterUser, bindingResult: BindingResult ): ResponseEntity<*> {

        if (registerUser.password != registerUser.passwordRepeated) {
            bindingResult.rejectValue("password", "Passwords do not match.")
            return ResponseEntity<BindingResultWrapper>(BindingResultWrapper(bindingResult), HttpStatus.BAD_REQUEST)
        }

        if (userRepository.findByUsernameIgnoreCase(registerUser.username) != null) {
            bindingResult.rejectValue("username", "Sorry, this username already exists.")
            return ResponseEntity<BindingResultWrapper>(BindingResultWrapper(bindingResult), HttpStatus.BAD_REQUEST)
        }

        userRepository.save(User(registerUser.username, registerUser.name, passwordEncoder.encode(registerUser.password)))
        return ResponseEntity<Void>(MultiValueMap.fromSingleValue(mapOf("Location"  to "/login")), HttpStatus.FOUND)
    }
}
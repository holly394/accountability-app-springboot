package com.github.holly.accountability.config.auth

import com.github.holly.accountability.user.AccountabilitySessionUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val userDetailsService: AccountabilityUserDetailsService,
    private val formLoginHandler: FormLoginHandler,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        antMatcher("/**/*.js"),
                        antMatcher("/**/*.css")
                    ).permitAll()
                    .requestMatchers(
                        antMatcher("/**/*.png"),
                        antMatcher("/**/*.jpg"),
                        antMatcher("/**/*.svg"),
                        antMatcher("/**/*.gif"),
                    ).permitAll()
                    .requestMatchers(
                        antMatcher("/**/*.woff"),
                        antMatcher("/**/*.woff2"),
                        antMatcher("/**/*.ttf"),
                        antMatcher("/**/*.otf"),
                    ).permitAll()
                    .requestMatchers(antMatcher("/**/favicon.ico")).permitAll()
                    .requestMatchers("/index.html" ,"/registration", "/h2-console/**", "/error").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().authenticated()
            }
            .formLogin {
                it
                    .loginPage("/login")
                    .loginProcessingUrl("/api/login")
                    .successHandler(formLoginHandler)
                    .failureHandler(formLoginHandler)
                    .permitAll()
            }
            .logout {
                it
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .permitAll()
            }
            .build()
    }

    @Controller
    class LoginController {

        // used to serve the correct route inside our VueJS application
        @GetMapping("/login")
        fun login(@AuthenticationPrincipal principal: Any): ModelAndView {
            if (principal is AccountabilitySessionUser) {
                return ModelAndView("redirect:/")
            }
            return ModelAndView("index.html")
        }
    }

}
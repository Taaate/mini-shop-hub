package com.example.minishophub.global.login.handler

import com.example.minishophub.domain.user.persistence.buyer.BuyerRepository
import com.example.minishophub.global.jwt.service.JwtService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class LoginSuccessHandler(
    private val jwtService: JwtService,
    private val buyerRepository: BuyerRepository,
) : SimpleUrlAuthenticationSuccessHandler() {

    private val log = KotlinLogging.logger { }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val email = extractUsername(authentication)
        val accessToken = jwtService.createAccessToken(email)
        val refreshToken = jwtService.createRefreshToken()

        log.info { "LoginSuccessHandler - onAuthenticationSuccess 시작" }

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken)

        val user = buyerRepository.findByEmail(email)
        if (user != null) {
            user.updateRefreshToken(refreshToken)
            buyerRepository.saveAndFlush(user)
        }

        log.info { "LoginSuccessHandler - onAuthenticationSuccess 종료" }
        log.info { "로그인에 성공했습니다. 이메일 : $email" }
        log.info { "로그인에 성공했습니다. AccessToken : $accessToken" }
    }

    private fun extractUsername(authentication: Authentication): String {
        val userDetails = authentication.principal as UserDetails
        return userDetails.username
    }
}
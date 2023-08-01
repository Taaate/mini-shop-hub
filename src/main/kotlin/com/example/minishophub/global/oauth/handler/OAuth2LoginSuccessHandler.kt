package com.example.minishophub.global.oauth.handler

import com.example.minishophub.domain.user.persistence.UserRole
import com.example.minishophub.global.jwt.service.JwtService
import com.example.minishophub.global.oauth.CustomOAuth2User
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginSuccessHandler(
    private val jwtService: JwtService,
) : AuthenticationSuccessHandler {

    private val log = KotlinLogging.logger { }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        log.info { "OAuth2LoginSuccessHandler - onAuthenticationSuccess 시작" }

        try {
            val oAuth2User = authentication.principal as CustomOAuth2User

            if (oAuth2User.role == UserRole.GUEST) {
                val accessToken = jwtService.createAccessToken(oAuth2User.email)
                response.addHeader(jwtService.accessHeader, "Bearer $accessToken")
                response.sendRedirect("/jwt-test")
                jwtService.sendAccessAndRefreshToken(response, accessToken, null)
            } else {
                loginSuccess(response, oAuth2User)
            }

        } catch (e: Exception) {
            throw e
        }

        log.info { "OAuth2LoginSuccessHandler - onAuthenticationSuccess 종료" }

    }

    private fun loginSuccess(response: HttpServletResponse, oAuth2User: CustomOAuth2User) {
        val accessToken: String = jwtService.createAccessToken(oAuth2User.email)
        val refreshToken: String = jwtService.createRefreshToken()
        response.addHeader(jwtService.accessHeader, "Bearer $accessToken")
        response.addHeader(jwtService.refreshHeader, "Bearer $refreshToken")

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken)
        jwtService.updateRefreshToken(oAuth2User.email, refreshToken)
    }

}
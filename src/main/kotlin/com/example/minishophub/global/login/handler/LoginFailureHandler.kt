package com.example.minishophub.global.login.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

class LoginFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_BAD_REQUEST
        response.characterEncoding = "UTF-8"
        response.contentType = "text/plain;charset=UTF-8"
        response.writer.write("로그인 실패! 이메일이나 비밀번호를 확인해주세요.")
        println("로그인에 실패했습니다. 메시지 : ${exception.message}")
    }

}
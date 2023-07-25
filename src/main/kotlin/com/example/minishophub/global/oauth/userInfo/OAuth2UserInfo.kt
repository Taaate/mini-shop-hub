package com.example.minishophub.global.oauth.userInfo

abstract class OAuth2UserInfo(
    val attributes: Map<String, Any>
) {

    abstract fun getId(): String?

    abstract fun getNickname(): String?

    abstract fun getEmail(): String?

}
package com.example.minishophub.global.oauth.userInfo


class GoogleOAuth2UserInfo(attributes: MutableMap<String, Any>) : OAuth2UserInfo(attributes) {
    override fun getId(): String? {
        return attributes["sub"] as String?
    }

    override fun getNickname(): String? {
        return attributes["name"] as String?
    }

    override fun getEmail(): String? {
        return (attributes["email"] as String?)!!
    }

}

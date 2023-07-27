package com.example.minishophub.domain.user.service

import com.example.minishophub.domain.shop.persistence.ShopRepository
import com.example.minishophub.domain.user.controller.dto.request.OAuth2UserUpdateRequest
import com.example.minishophub.domain.user.controller.dto.request.UserJoinRequest
import com.example.minishophub.domain.user.controller.dto.request.UserUpdateRequest
import com.example.minishophub.domain.user.persistence.user.User
import com.example.minishophub.domain.user.persistence.user.UserRepository
import com.example.minishophub.domain.user.persistence.UserRole
import com.example.minishophub.domain.user.persistence.follow.Follow
import com.example.minishophub.domain.user.persistence.follow.FollowRepository
import com.example.minishophub.global.util.fail
import com.example.minishophub.global.util.findByIdOrThrow
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BuyerService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val shopRepository: ShopRepository,
    private val followRepository: FollowRepository,
) {

    @Transactional
    fun join(joinRequest: UserJoinRequest) {

        checkEmail(joinRequest.email)
        checkNickname(joinRequest.nickname)

        val user = User(
            email = joinRequest.email,
            password = joinRequest.password,
            nickname = joinRequest.nickname,
            age = joinRequest.age,
            city = joinRequest.city,
            role = UserRole.USER,
        )

        user.passwordEncode(passwordEncoder)
        userRepository.save(user)
    }

    fun find(userId: Long): User {
        return userRepository.findByIdOrThrow(userId)
    }

    @Transactional
    fun update(userId: Long, updateRequest: UserUpdateRequest) {
        val user = userRepository.findByIdOrThrow(userId)

        checkEmail(updateRequest.email)
        checkNickname(updateRequest.nickname)

        user.update(updateRequest)
    }

    @Transactional
    fun delete(userId: Long) {
        userRepository.deleteById(userId)
    }

    @Transactional
    fun updateOAuth2(updateRequest: OAuth2UserUpdateRequest, email: String) {
        val user = userRepository.findByEmail(email)!!
        user.updateOAuth(updateRequest)
    }

    @Transactional
    fun followShop(shopId: Long, email: String) {
        val followedShop = shopRepository.findById(shopId).get()
        val follower = userRepository.findByEmail(email) ?: fail()

        if (followRepository.existsByUserAndShop(follower, followedShop)) {
            followRepository.deleteByUserAndShop(follower, followedShop)
            followedShop.followerDecrease()
            return
        }
        followRepository.save(Follow(
            user = follower,
            shop = followedShop
        ))
        followedShop.followerIncrease()
    }

    private fun checkEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 존재하는 이메일 입니다.")
        }
    }

    private fun checkNickname(nickname: String) {
        if (userRepository.existsByNickname(nickname)) {
            throw IllegalArgumentException("이미 존재하는 닉네임 입니다.")
        }
    }


}

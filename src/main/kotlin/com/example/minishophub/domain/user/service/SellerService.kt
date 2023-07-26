package com.example.minishophub.domain.user.service

import com.example.minishophub.domain.shop.persistence.Shop
import com.example.minishophub.domain.user.controller.dto.request.SellerApplyRequest
import com.example.minishophub.domain.user.controller.dto.request.UserUpdateRequest
import com.example.minishophub.domain.user.persistence.UserRole
import com.example.minishophub.domain.user.persistence.seller.Seller
import com.example.minishophub.domain.user.persistence.seller.SellerRepository
import com.example.minishophub.domain.user.persistence.buyer.BuyerRepository
import com.example.minishophub.global.util.fail
import com.example.minishophub.global.util.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SellerService(
    private val sellerRepository: SellerRepository,
    private val buyerRepository: BuyerRepository,
) {

    @Transactional
    fun changeToSeller(email: String, applyRequest: SellerApplyRequest): Seller {
        val owner = buyerRepository.findByEmail(email) ?: fail()
        if (owner.role != UserRole.USER) {
            throw IllegalArgumentException("추가 인증이 안되어 있는 유저 입니다.")
        }
        val seller = Seller(
            email = owner.email,
            password = owner.password,
            nickname = owner.nickname,
            age = owner.age,
            city = owner.city,
            socialId = owner.socialId,
            socialType = owner.socialType,
            refreshToken = owner.refreshToken,
            businessRegistrationNumber = applyRequest.businessRegistrationNumber,
            myShop = Shop.defaultShop(owner.id)
        )

        buyerRepository.deleteById(owner.id)

        return sellerRepository.save(seller)
    }

    fun find(sellerId: Long): Seller = sellerRepository.findById(sellerId).get()

    @Transactional
    fun update(sellerId: Long, updateRequest: UserUpdateRequest) {
        val seller = sellerRepository.findByIdOrThrow(sellerId)

        checkEmail(updateRequest.email)
        checkNickname(updateRequest.nickname)

        seller.update(updateRequest)

    }

    private fun checkEmail(email: String) {
        if (buyerRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 존재하는 이메일 입니다.")
        }
    }

    private fun checkNickname(nickname: String) {
        if (buyerRepository.existsByNickname(nickname)) {
            throw IllegalArgumentException("이미 존재하는 닉네임 입니다.")
        }
    }

    fun deleteSeller(sellerId: Long) = sellerRepository.deleteById(sellerId)

}
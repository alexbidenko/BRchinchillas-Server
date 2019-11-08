package ru.chinchillas.br.brchinchillas.users

import org.springframework.data.jpa.repository.JpaRepository

interface BlockedUsersRepository : JpaRepository<BlockedUser, Long> {
    fun existsByEmailOrPhoneOrAddress(email: String, phone: String, address: String): Boolean

    fun existsByUserId(userId: Long): Boolean
}
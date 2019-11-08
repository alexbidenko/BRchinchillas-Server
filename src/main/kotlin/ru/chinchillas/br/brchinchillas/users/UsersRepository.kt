package ru.chinchillas.br.brchinchillas.users

import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun findOneByEmail(email: String): User?
}
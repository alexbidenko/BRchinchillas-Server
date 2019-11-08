package ru.chinchillas.br.brchinchillas.users

import org.springframework.data.jpa.repository.JpaRepository

interface TokensRepository : JpaRepository<Token, Long> {
    fun findOneByValue(value: String): Token?
}
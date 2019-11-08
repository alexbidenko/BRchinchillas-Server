package ru.chinchillas.br.brchinchillas.chinchillas

import org.springframework.data.jpa.repository.JpaRepository

interface ChinchillasRepository : JpaRepository<Chinchilla, Long> {

    fun findAllByStatus(status: Int): MutableList<Chinchilla>

    fun findAllByStatusAndBirthdayBetween(status: Int, birthdayStart: Long, birthdayEnd: Long): MutableList<Chinchilla>

    fun findAllByIdIsContaining(idList: List<Long>): MutableList<Chinchilla>
}
package ru.chinchillas.br.brchinchillas.users

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Token (
        val userId: Long,
        val value: String,
        var time: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}
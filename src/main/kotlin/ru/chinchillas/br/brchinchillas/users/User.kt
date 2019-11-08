package ru.chinchillas.br.brchinchillas.users

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User (
        var firstName: String,
        var lastName: String,
        var city: String,
        var email: String,
        var phone: String,
        var password: String,
        var isCheckedEmail: Boolean
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var lastActive: Long = 0
}
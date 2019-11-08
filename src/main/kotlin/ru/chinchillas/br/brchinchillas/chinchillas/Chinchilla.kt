package ru.chinchillas.br.brchinchillas.chinchillas

import java.util.*
import javax.persistence.*

@Entity
class Chinchilla(
        var name: String,
        @Column(length=100000)
        @Lob
        var description: String,
        var birthday: Long,
        var adultAvatar: String,
        var babyAvatar: String,
        var adultPhotos: ArrayList<String>,
        var babyPhotos: ArrayList<String>,
        var mother: Long?,
        var father: Long?,
        var status: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}
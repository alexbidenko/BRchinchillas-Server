package ru.chinchillas.br.brchinchillas.purchases

import javax.persistence.*

@Entity
class Purchase (
        var chinchillaId: Long,
        @Column(length = 100000)
        @Lob
        var description: String,
        var rubles: Int,
        var euros: Int,
        var status: Int
) {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0
}
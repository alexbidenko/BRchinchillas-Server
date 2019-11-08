package ru.chinchillas.br.brchinchillas.purchases

import org.springframework.data.jpa.repository.JpaRepository

interface PurchasesRepository : JpaRepository<Purchase, Long>
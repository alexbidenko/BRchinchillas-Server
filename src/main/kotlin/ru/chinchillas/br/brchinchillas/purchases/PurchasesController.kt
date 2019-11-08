package ru.chinchillas.br.brchinchillas.purchases

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/purchases")
@CrossOrigin(origins = ["*"])
class PurchasesController (
        internal val purchasesRepository: PurchasesRepository
) {

    @GetMapping("/get")
    fun getPurchases(): ResponseEntity<List<Purchase>> {
        return ResponseEntity(purchasesRepository.findAll(), HttpStatus.OK)
    }

    @PostMapping("/add")
    fun addPurchase(@RequestBody newPurchase: Purchase): ResponseEntity<String> {
        purchasesRepository.save(newPurchase)
        return ResponseEntity("""{"id":${newPurchase.id}}""", HttpStatus.CREATED)
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updatePurchase(
            @PathVariable("id") id: Long,
            @RequestBody newPurchase: Purchase
    ) {
        val purchase = purchasesRepository.getOne(id)
        purchase.chinchillaId = newPurchase.chinchillaId
        purchase.description = newPurchase.description
        purchase.euros = newPurchase.euros
        purchase.rubles = newPurchase.rubles
        purchase.status = newPurchase.status
        purchasesRepository.save(purchase)
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun deletePurchase(@PathVariable("id") id: Long) {
        purchasesRepository.deleteById(id)
    }
}
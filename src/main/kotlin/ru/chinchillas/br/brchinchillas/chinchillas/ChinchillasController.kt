package ru.chinchillas.br.brchinchillas.chinchillas

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.chinchillas.br.brchinchillas.images.StorageService
import java.util.*

@RestController
@RequestMapping("/chinchillas")
@CrossOrigin(origins = ["*"])
class ChinchillasController (
        internal val chinchillasRepository: ChinchillasRepository
) {

    @GetMapping("/all")
    fun getAllChinchillas(): ResponseEntity<List<Chinchilla>> {
        return ResponseEntity(chinchillasRepository.findAll(), HttpStatus.OK)
    }

    @GetMapping("/get")
    fun getChinchillas(
            @RequestParam("status") status: Int,
            @RequestParam("year", required = false) year: Int?
    ): ResponseEntity<List<Chinchilla>> {
        val chinchillas = if(year != null) {
            val calendar = Calendar.getInstance()
            calendar.set(year, 0, 1)
            val birthdayStart = calendar.timeInMillis
            calendar.set(year, 11, 31)
            val birthdayEnd = calendar.timeInMillis
            chinchillasRepository.findAllByStatusAndBirthdayBetween(status, birthdayStart, birthdayEnd)
        } else {
            chinchillasRepository.findAllByStatus(status)
        }
        return ResponseEntity(chinchillas, HttpStatus.OK)
    }

    @GetMapping("/get-by-ids/{ids}")
    fun getChinchillasByIds(@PathVariable("ids") ids: List<Long>): ResponseEntity<List<Chinchilla>> {
        return ResponseEntity(chinchillasRepository.findAllByIdIsContaining(ids), HttpStatus.OK)
    }

    @PostMapping("/add")
    fun addChinchilla(@RequestBody newChinchilla: Chinchilla): ResponseEntity<String> {
        newChinchilla.adultPhotos.forEach {
            StorageService.replaceFile(it)
        }
        newChinchilla.babyPhotos.forEach {
            StorageService.replaceFile(it)
        }
        chinchillasRepository.save(newChinchilla)
        return ResponseEntity("""{"id":${newChinchilla.id}}""", HttpStatus.CREATED)
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateChinchilla(
            @PathVariable("id") id: Long,
            @RequestBody newChinchilla: Chinchilla
    ) {
        val chinchilla = chinchillasRepository.getOne(id)
        chinchilla.adultAvatar = newChinchilla.adultAvatar
        chinchilla.babyAvatar = newChinchilla.babyAvatar
        chinchilla.babyPhotos = newChinchilla.babyPhotos
        chinchilla.birthday = newChinchilla.birthday
        chinchilla.description = newChinchilla.description
        chinchilla.father = newChinchilla.father
        chinchilla.mother = newChinchilla.mother
        chinchilla.name = newChinchilla.name

        chinchilla.adultPhotos.forEach {
            if(!newChinchilla.adultPhotos.contains(it)) {
                StorageService.deleteFile(it)
            }
        }
        newChinchilla.adultPhotos.forEach {
            if(!chinchilla.adultPhotos.contains(it)) {
                StorageService.replaceFile(it)
            }
        }
        chinchilla.adultPhotos = newChinchilla.adultPhotos

        chinchilla.babyPhotos.forEach {
            if(!newChinchilla.babyPhotos.contains(it)) {
                StorageService.deleteFile(it)
            }
        }
        newChinchilla.babyPhotos.forEach {
            if(!chinchilla.babyPhotos.contains(it)) {
                StorageService.replaceFile(it)
            }
        }
        chinchilla.babyPhotos = newChinchilla.babyPhotos

        chinchilla.status = newChinchilla.status
        chinchillasRepository.save(chinchilla)
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteChinchilla(@PathVariable("id") id: Long) {
        val chinchilla = chinchillasRepository.getOne(id)
        chinchilla.adultPhotos.forEach {
            StorageService.deleteFile(it)
        }
        chinchilla.babyPhotos.forEach {
            StorageService.deleteFile(it)
        }
        chinchillasRepository.deleteById(id)
    }
}
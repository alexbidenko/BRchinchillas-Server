package ru.chinchillas.br.brchinchillas.images

import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.GetMapping

@RestController
@RequestMapping("/photos")
@CrossOrigin(origins = ["*"])
class ImagesController {

    @GetMapping("/get/{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = StorageService.loadAsResource(filename)
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.filename + "\"").body(file)
    }

    @PostMapping("/add")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val mine = file.originalFilename!!.substringAfterLast(".").toLowerCase()
        return if (mine != "jpg" && mine != "jpeg" && mine != "png") {
            ResponseEntity(HttpStatus.FORBIDDEN)
        } else {
            val filename = System.currentTimeMillis().toString() + "." + mine
            StorageService.store(file, filename)
            ResponseEntity("""{"filename":"$filename"}""", HttpStatus.OK)
        }
    }
}
package ru.chinchillas.br.brchinchillas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@SpringBootApplication
class BrchinchillasApplication

fun main(args: Array<String>) {
    runApplication<BrchinchillasApplication>(*args)
    // nohup java -jar brchinchillas-0.2.2.jar 2>&1 > brchinchillas.log &
    // 14048
    /*val username = "alexbidenko1998@gmail.com"
    val password = "Alex2112_2012alex"

    val props = Properties()
    props["mail.smtp.host"] = "smtp.gmail.com"
    props["mail.smtp.auth"] = "true"
    props["mail.smtp.port"] = "465"
    props["mail.smtp.starttls.enable"] = "true"
    props["mail.smtp.socketFactory.port"] = "465"
    props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
    props["mail.smtp.socketFactory.fallback"] = "false"

    val session = Session.getInstance(props, object : Authenticator() {

        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(username, password)
        }
    })

    val message = MimeMessage(session)
    //от кого
    message.setFrom(InternetAddress(username))
    //кому
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("alexbidenko1998@gmail.com"))
    //тема сообщения
    message.subject = "Это сообщение от величайшкго разработчика всех вемен"
    //текст
    message.setText("Трам-трам-трам, это не спам, напиши мне, если оно придет к тебе")

    //отправляем сообщение
    Transport.send(message)*/
}
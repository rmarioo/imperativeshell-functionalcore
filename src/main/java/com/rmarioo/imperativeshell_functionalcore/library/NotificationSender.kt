package com.rmarioo.imperativeshell_functionalcore.library

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
open class NotificationSender {
    @Autowired
    private val javaMailSender: JavaMailSender? = null
    data class Email(val recipients: Array<String?>?= null,val from: String?="",val subject: String?="",val content: String?="")

    open fun sendMail(
        email: Email
    ) {

        val (recipients, from, subject, content) = email
        val mimeMessage = javaMailSender!!.createMimeMessage()
        val messageHelper = MimeMessageHelper(mimeMessage)
        try {
            messageHelper.setTo(recipients)
            messageHelper.setFrom(from)
            messageHelper.setSubject(subject)
            messageHelper.setText(content, false)
            javaMailSender.send(mimeMessage)
            log.info(
                "Email successfully sent. \n From: {} \n To: {} \n Subject: {} \n Content: {}",
                mimeMessage.from,
                StringUtils.arrayToCommaDelimitedString(mimeMessage.allRecipients),
                mimeMessage.subject,
                mimeMessage.content.toString()
            )
        } catch (e: Exception) {
            log.error("Error occurred while sending email", e)
            throw RuntimeException(e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(NotificationSender::class.java)
    }
}

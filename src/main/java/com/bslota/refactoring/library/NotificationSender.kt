package com.bslota.refactoring.library

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class NotificationSender {
    @Autowired
    private val javaMailSender: JavaMailSender? = null
    fun sendMail(recipients: Array<String?>?, from: String?, subject: String?, content: String?) {
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

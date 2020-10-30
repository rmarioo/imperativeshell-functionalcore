package com.rmarioo.imperativeshell_functionalcore.library

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.subethamail.wiser.Wiser

@Configuration
open class SmtpConfig {
    @Bean
    open fun wiser(): Wiser {
        val wiser = Wiser()
        wiser.setHostname("localhost")
        wiser.setPort(10025)
        wiser.start()
        return wiser
    }
}

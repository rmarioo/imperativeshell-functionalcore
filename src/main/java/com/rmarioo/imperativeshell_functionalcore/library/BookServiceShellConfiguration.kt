package com.rmarioo.imperativeshell_functionalcore.library

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BookServiceShellConfiguration {


    @Bean
    open fun placeOnHoldCoreFuction() : (PlaceOnHoldRequest) -> ResultWithEffectsDescription =
        { request: PlaceOnHoldRequest -> placeOnHoldCore(request)  }

}

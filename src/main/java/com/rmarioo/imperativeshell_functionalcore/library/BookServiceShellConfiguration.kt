package com.rmarioo.imperativeshell_functionalcore.library

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Optional

@Configuration
open class BookServiceShellConfiguration {


    @Bean
    open fun placeOnHoldCoreFuction() : (PlaceOnHoldRequest) -> BookOnHoldResult =
        { request: PlaceOnHoldRequest -> placeOnHoldCore(request)  }

}

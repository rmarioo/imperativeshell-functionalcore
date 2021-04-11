package com.rmarioo.imperativeshell_functionalcore.library

import com.rmarioo.imperativeshell_functionalcore.library.core.BookOnHoldResult
import com.rmarioo.imperativeshell_functionalcore.library.core.PlaceOnHoldRequest
import com.rmarioo.imperativeshell_functionalcore.library.core.placeOnHoldCore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BookServiceShellConfiguration {


    @Bean
    open fun placeOnHoldCoreFuction() : (PlaceOnHoldRequest) -> BookOnHoldResult =
        { request: PlaceOnHoldRequest -> placeOnHoldCore(request)  }

}

package de.larmic.ddd.infrastructure.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldRestController {

    @GetMapping(value = ["/api/hello"], produces = ["text/plain"])
    fun sayHello() = "Hello World!"

}
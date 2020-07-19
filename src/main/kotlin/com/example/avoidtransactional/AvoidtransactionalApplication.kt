package com.example.avoidtransactional

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
class AvoidtransactionalApplication

fun main(args: Array<String>) {
	runApplication<AvoidtransactionalApplication>(*args)
}

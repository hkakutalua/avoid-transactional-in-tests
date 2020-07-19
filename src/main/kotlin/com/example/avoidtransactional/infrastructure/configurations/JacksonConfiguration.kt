package com.example.avoidtransactional.infrastructure.configurations

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {
    @Bean
    fun jacksonKotlinModule() = KotlinModule()
}
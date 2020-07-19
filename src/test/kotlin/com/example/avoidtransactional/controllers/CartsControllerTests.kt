package com.example.avoidtransactional.controllers

import com.example.avoidtransactional.domain.model.Cart
import com.example.avoidtransactional.domain.model.Product
import com.example.avoidtransactional.infrastructure.repositories.CartsRepository
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

@SpringBootTest
internal class CartsControllerTests {
    @Autowired
    lateinit var cartsRepository: CartsRepository

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    lateinit var mockMvc: MockMvc

    lateinit var product: Product

    @BeforeEach
    fun beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun createCart() {
        val servletResponse = mockMvc.perform(post("/carts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(isA<String>(String::class.java)))
            .andReturn().response

        val cartId = UUID.fromString(JsonPath.read(servletResponse.contentAsString, "$.id"))
        assertThat(cartsRepository.existsById(cartId)).isTrue()
    }

    @Test
    fun getAllCarts() {
        val carts = listOf(Cart(), Cart())
        cartsRepository.saveAll(carts)

        mockMvc.perform(get("/carts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.[*].id").value(hasItem(carts[0].id.toString())))
    }
}
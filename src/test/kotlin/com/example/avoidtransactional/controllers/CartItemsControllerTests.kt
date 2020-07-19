package com.example.avoidtransactional.controllers

import com.example.avoidtransactional.controllers.inputmodels.CartItemInputModel
import com.example.avoidtransactional.domain.model.Cart
import com.example.avoidtransactional.domain.model.Product
import com.example.avoidtransactional.infrastructure.repositories.CartsRepository
import com.example.avoidtransactional.infrastructure.repositories.ProductsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.util.*
import javax.transaction.Transactional

@SpringBootTest
@Transactional
internal class CartItemsControllerTests {
    @Autowired
    lateinit var cartsRepository: CartsRepository

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    lateinit var mockMvc: MockMvc

    lateinit var product: Product

    @BeforeEach
    fun beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()

        product = Product("Coca-Cola", BigDecimal.valueOf(200))
        productsRepository.save(product)
    }

    @Test
    fun getAllCartItems() {
        val cart = Cart()
        cart.addProduct(product, 3)
        cartsRepository.save(cart)

        mockMvc.perform(get("/carts/{id}/items", cart.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.items[0].id.toString())))
            .andExpect(jsonPath("$.[*].product.id").value(hasItem(product.id.toString())))
            .andExpect(jsonPath("$.[*].product.name").value(hasItem(product.name)))
            .andExpect(jsonPath("$.[*].quantity").value(3))
    }

    @Test
    fun addItemToCart() {
        val cart = Cart()
        cartsRepository.save(cart)

        val itemQuantity = 5
        val cartItemInput = CartItemInputModel(product.id, itemQuantity)

        val servletResponse = mockMvc.perform(post("/carts/{id}/items", cart.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(toJsonString(cartItemInput)))
            .andExpect(jsonPath("$.id").value(isA<String>(String::class.java)))
            .andExpect(jsonPath("$.product.id").value(product.id.toString()))
            .andExpect(jsonPath("$.product.name").value(product.name))
            .andExpect(jsonPath("$.quantity").value(itemQuantity))
            .andReturn().response

        val updatedCart = cartsRepository.findByIdOrNull(cart.id)
        val itemId = UUID.fromString(JsonPath.read(servletResponse.contentAsString, "$.id"))
        val cartItem = updatedCart?.items?.find { x -> x.id == itemId }

        assertThat(cartItem?.quantity).isEqualTo(itemQuantity)
        assertThat(cartItem?.product).isEqualTo(product)
    }

    private fun toJsonString(`object`: Any): String {
        val objectMapper = ObjectMapper()
        objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE

        return objectMapper.writeValueAsString(`object`)
    }
}
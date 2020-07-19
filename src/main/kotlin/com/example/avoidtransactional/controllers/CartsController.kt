package com.example.avoidtransactional.controllers

import com.example.avoidtransactional.controllers.viewmodels.CartViewModel
import com.example.avoidtransactional.domain.model.Cart
import com.example.avoidtransactional.infrastructure.repositories.CartsRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("carts")
class CartsController(private val cartsRepository: CartsRepository) {
    @PostMapping
    fun createCart(): ResponseEntity<Any> {
        val cart = Cart()
        cartsRepository.save(cart)

        return ResponseEntity.ok(CartViewModel(cart.id))
    }

    @GetMapping
    fun getAllCarts(): ResponseEntity<Any> {
        return ResponseEntity.ok(cartsRepository.findAll().map { cart -> CartViewModel(cart.id) })
    }
}
package com.example.avoidtransactional.controllers

import com.example.avoidtransactional.controllers.inputmodels.CartItemInputModel
import com.example.avoidtransactional.controllers.viewmodels.CartItemViewModel
import com.example.avoidtransactional.domain.model.ProductItem
import com.example.avoidtransactional.infrastructure.repositories.CartsRepository
import com.example.avoidtransactional.infrastructure.repositories.ProductsRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("carts/{cart_id}/items")
class CartItemsController(
    private val cartsRepository: CartsRepository,
    private val productsRepository: ProductsRepository) {

    @PostMapping
    fun addItemToCart(
        @PathVariable("cart_id") cartId: UUID,
        @RequestBody cartItem: CartItemInputModel
    ) : ResponseEntity<Any> {
        val product =  productsRepository.findByIdOrNull(cartItem.productId)
        if (product == null) {
            return ResponseEntity.notFound().build()
        }

        val cart = cartsRepository.findByIdOrNull(cartId)
        if (cart == null) {
            return ResponseEntity.notFound().build()
        }

        cart.addProduct(product, cartItem.quantity)
        cartsRepository.save(cart) // with @Transactional there's no need of this line

        val savedProductItem = cart.items.first { x -> x.product.id == cartItem.productId }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapToCartItemViewModel(savedProductItem))
    }

    @GetMapping
    fun getAllItems(@PathVariable("cart_id") cartId: UUID): ResponseEntity<Any> {
        val cart = cartsRepository.findByIdOrNull(cartId)
        if (cart == null) {
            return ResponseEntity.notFound().build()
        }

        val mappedItems = cart.items.map { item -> mapToCartItemViewModel(item) }

        return ResponseEntity.ok(mappedItems)
    }

    private fun mapToCartItemViewModel(item: ProductItem): CartItemViewModel {
        return CartItemViewModel(
            id = item.id,
            product = CartItemViewModel.InnerProductViewModel(item.product.id, item.product.name),
            quantity = item.quantity)
    }
}
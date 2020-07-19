package com.example.avoidtransactional.domain.model

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "carts")
class Cart : DomainEntity() {
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _items = mutableListOf<ProductItem>()

    val items: List<ProductItem>
        get() = _items

    val totalPrice: BigDecimal
        get() {
            if (_items.isNotEmpty()) {
                return _items
                    .map { i -> i.totalPrice }
                    .reduce { sum, itemPrice -> sum + itemPrice }
            }

            return BigDecimal.ZERO
        }

    fun addProduct(product: Product, quantity: Int) {
        if (_items.any { item -> item.product == product }) {
            val existentItem = _items.first { item -> item.product == product }
            existentItem.quantity = quantity
        } else {
            _items.add(ProductItem(product, quantity))
        }
    }
}
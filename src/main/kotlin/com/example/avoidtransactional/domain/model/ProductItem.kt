package com.example.avoidtransactional.domain.model

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "cart_products_items")
class ProductItem (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    quantity: Int

) : DomainEntity() {
    @Column(name = "quantity", nullable = false)
    var quantity = quantity
        set(value) {
            require(value > 0)
            field = value
        }

    val totalPrice
        get() = product.price.multiply(BigDecimal(quantity))
}

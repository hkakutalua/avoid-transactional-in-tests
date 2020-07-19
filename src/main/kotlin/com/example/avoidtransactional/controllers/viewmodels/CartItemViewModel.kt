package com.example.avoidtransactional.controllers.viewmodels

import java.util.*

data class CartItemViewModel(
    val id: UUID,
    val product: InnerProductViewModel,
    val quantity: Int) {
    data class InnerProductViewModel(val id: UUID, val name: String)
}
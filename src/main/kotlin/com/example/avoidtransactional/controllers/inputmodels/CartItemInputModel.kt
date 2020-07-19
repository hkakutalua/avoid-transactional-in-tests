package com.example.avoidtransactional.controllers.inputmodels

import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class CartItemInputModel(
    @NotNull
    val productId: UUID,

    @Min(1)
    val quantity: Int
)

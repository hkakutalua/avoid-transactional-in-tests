package com.example.avoidtransactional.infrastructure.repositories

import com.example.avoidtransactional.domain.model.Cart
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CartsRepository : CrudRepository<Cart, UUID>
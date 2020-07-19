package com.example.avoidtransactional.infrastructure.repositories

import com.example.avoidtransactional.domain.model.Product
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ProductsRepository : CrudRepository<Product, UUID>
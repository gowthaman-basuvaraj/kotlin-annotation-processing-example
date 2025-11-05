package com.example.app

import com.example.annotations.GenerateDto
import com.example.annotations.Generated

/**
 * Sample product entity.
 *
 * Shows how annotation processing works with different fields.
 * Password is excluded from DTOs.
 */
@GenerateDto(exclude = ["internalCode"])
data class Product(
    @Generated
    val id: Long,
    val name: String,
    val price: Double,
    val inStock: Boolean,
    val internalCode: String
)

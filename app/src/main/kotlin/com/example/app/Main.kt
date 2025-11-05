package com.example.app

/**
 * Main application entry point.
 *
 * Demonstrates using generated DTOs.
 * Run this after building the project.
 */
fun main() {
    println("Annotation Processing Sample")
    println("=============================")
    println()

    // Create a user entity
    val user = User(
        id = 1L,
        name = "John Smith",
        email = "john@example.com",
        age = 25
    )

    println("Original User Entity:")
    println(user)
    println()

    // Convert to read DTO
    val userReadDto = user.toReadDto()
    println("User Read DTO:")
    println(userReadDto)
    println()

    // Create from write DTO
    val userWriteDto = UserWriteDto(
        name = "Jane Doe",
        email = "jane@example.com",
        age = 30
    )
    println("User Write DTO:")
    println(userWriteDto)
    println()

    val newUser = userWriteDto.toEntity(id = 2L)
    println("User created from Write DTO:")
    println(newUser)
    println()

    // Product example
    val product = Product(
        id = 100L,
        name = "Laptop",
        price = 999.99,
        inStock = true,
        internalCode = "SECRET123"
    )

    println("Original Product Entity:")
    println(product)
    println()

    val productReadDto = product.toReadDto()
    println("Product Read DTO (internal code excluded):")
    println(productReadDto)
    println()

    val productWriteDto = ProductWriteDto(
        name = "Mouse",
        price = 29.99,
        inStock = true
    )

    println("Product Write DTO (no internal code):")
    println(productWriteDto)
    println()

    println("Build completed successfully!")
    println("Check build/generated/source/kapt/main for generated files")
}

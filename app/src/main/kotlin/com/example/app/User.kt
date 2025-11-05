package com.example.app

import com.example.annotations.GenerateDto
import com.example.annotations.Generated

/**
 * Sample user entity.
 *
 * This class demonstrates annotation processing.
 * The processor will generate UserWriteDto and UserReadDto.
 */
@GenerateDto
data class User(
    @Generated
    val id: Long,
    val name: String,
    val email: String,
    val age: Int?
)

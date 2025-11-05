package com.example.annotations

/**
 * Marks an entity class for DTO generation.
 *
 * The processor will create write and read DTOs.
 * Write DTOs exclude generated fields like IDs.
 * Read DTOs include all fields.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateDto(
    /**
     * Generate write DTO for creating and updating.
     */
    val writeDto: Boolean = true,

    /**
     * Generate read DTO for responses.
     */
    val readDto: Boolean = true,

    /**
     * Fields to exclude from DTOs.
     */
    val exclude: Array<String> = []
)

/**
 * Marks a field as generated.
 *
 * Generated fields are excluded from write DTOs.
 * They are included in read DTOs.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Generated

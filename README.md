# Annotation Processing Sample Project

## Overview

This project shows annotation processing in action. It generates DTO classes from annotated entities.

## Project Structure

The project has three modules.

### annotations

Contains annotation definitions. These mark classes for processing.

### processor

Contains the annotation processor. It reads annotations and generates code.

### app

Contains sample entities. It uses the annotations. Generated code appears here.

## How to Build

Run Gradle build command.

```bash
./gradlew build
```

The processor runs during compilation. It generates DTO classes. Check build/generated/source/kapt/main.

## How to Run

Execute the main class.

```bash
./gradlew :app:run
```

It creates instances of generated classes. Prints them to console.

## What Gets Generated

For each entity with GenerateDto annotation:

1. A write DTO class for input
2. A read DTO class for output
3. Extension functions for conversion

## Example

Input entity:

```kotlin
@GenerateDto
data class User(
    val id: Long,
    val name: String,
    val email: String
)
```

Generated write DTO:

```kotlin
data class UserWriteDto(
    val name: String,
    val email: String
)
```

Generated read DTO:

```kotlin
data class UserReadDto(
    val id: Long,
    val name: String,
    val email: String
)
```

Generated extensions:

```kotlin
fun User.toReadDto(): UserReadDto = UserReadDto(id, name, email)

fun UserWriteDto.toEntity(id: Long): User = User(id, name, email)
```

## Key Files

- annotations/src/main/kotlin/GenerateDto.kt - Annotation definition
- processor/src/main/kotlin/DtoProcessor.kt - Processor implementation
- app/src/main/kotlin/User.kt - Sample entity
- build/generated/source/kapt/main - Generated code location

## Learning Points

Study the processor code. See how it finds annotated classes. See how it extracts field information. See how it generates code.

Run the build with --info flag. See processor messages. Understand the generation flow.

Modify the User entity. Add or remove fields. Rebuild and see updated DTOs.

Add your own entity. Annotate it. Generate DTOs automatically.

# Examples of Generated Code

## Overview

This document shows what the processor generates. After building the project, check build/generated/source/kapt/main.

## User Entity

### Source Code

```kotlin
@GenerateDto
data class User(
    @Generated
    val id: Long,
    val name: String,
    val email: String,
    val age: Int?
)
```

### Generated UserWriteDto

```kotlin
package com.example.app

data class UserWriteDto(
    val name: String,
    val email: String,
    val age: Int?
)
```

The ID field is excluded. It is marked with Generated annotation. Write DTOs never include generated fields.

### Generated UserReadDto

```kotlin
package com.example.app

data class UserReadDto(
    val id: Long,
    val name: String,
    val email: String,
    val age: Int?
)
```

All fields are included. Read DTOs return complete data. Clients need all information.

### Generated UserExtensions

```kotlin
package com.example.app

fun User.toReadDto(): UserReadDto = UserReadDto(
    id = this.id,
    name = this.name,
    email = this.email,
    age = this.age
)

fun UserWriteDto.toEntity(id: Long): User = User(
    id = id,
    name = this.name,
    email = this.email,
    age = this.age
)
```

Extension functions provide conversions. Type-safe and efficient. No reflection needed.

## Product Entity

### Source Code

```kotlin
@GenerateDto(exclude = ["internalCode"])
data class Product(
    @Generated
    val id: Long,
    val name: String,
    val price: Double,
    val inStock: Boolean,
    val internalCode: String
)
```

The internalCode is sensitive. It is excluded via annotation parameter. Not in DTOs.

### Generated ProductWriteDto

```kotlin
package com.example.app

data class ProductWriteDto(
    val name: String,
    val price: Double,
    val inStock: Boolean
)
```

Both id and internalCode are excluded. ID is generated. internalCode is sensitive.

### Generated ProductReadDto

```kotlin
package com.example.app

data class ProductReadDto(
    val id: Long,
    val name: String,
    val price: Double,
    val inStock: Boolean
)
```

ID is included in read DTO. But internalCode is still excluded. Stays hidden from clients.

### Generated ProductExtensions

```kotlin
package com.example.app

fun Product.toReadDto(): ProductReadDto = ProductReadDto(
    id = this.id,
    name = this.name,
    price = this.price,
    inStock = this.inStock
)

fun ProductWriteDto.toEntity(id: Long, internalCode: String): Product = Product(
    id = id,
    name = this.name,
    price = this.price,
    inStock = this.inStock,
    internalCode = internalCode
)
```

The toEntity function needs both generated and excluded fields. They are passed as parameters. Not in the DTO itself.

## How to View Generated Files

Build the project first.

```bash
./gradlew build
```

Navigate to generated sources.

```bash
cd app/build/generated/source/kapt/main
```

List generated files.

```bash
find . -name "*.kt"
```

Expected output:

```
./com/example/app/UserWriteDto.kt
./com/example/app/UserReadDto.kt
./com/example/app/UserExtensions.kt
./com/example/app/ProductWriteDto.kt
./com/example/app/ProductReadDto.kt
./com/example/app/ProductExtensions.kt
```

Open any file to see generated code.

```bash
cat ./com/example/app/UserWriteDto.kt
```

## Key Observations

### Generated Fields Handling

Fields marked with Generated are excluded from write DTOs. They appear in read DTOs. This makes sense for auto-generated values.

### Explicit Exclusions

The exclude parameter removes fields from all DTOs. Both write and read. For sensitive data.

### Type Preservation

Nullable types stay nullable. Required types stay required. The processor preserves nullability correctly.

### Extension Functions

Conversions are bidirectional. Entity to read DTO is simple. Write DTO to entity needs extra parameters.

### Package Structure

Generated files use same package as source. They are part of the same module. Import works naturally.

## Testing Generated Code

The Main.kt file demonstrates usage. Run the application.

```bash
./gradlew :app:run
```

See DTOs in action. Creation and conversion. Verify correct behavior.

## Debugging Tips

Enable verbose KAPT output. Edit gradle.properties.

```properties
kapt.verbose=true
```

Rebuild and check logs.

```bash
./gradlew clean build --info
```

Look for processor messages. They show what gets generated. And any errors encountered.

Check generated source folder. If empty, processor did not run. Or encountered errors.

Verify annotation retention. Must be SOURCE for compile-time processing. RUNTIME is for reflection.

Ensure processor is registered. Check META-INF/services file. Class name must be fully qualified.

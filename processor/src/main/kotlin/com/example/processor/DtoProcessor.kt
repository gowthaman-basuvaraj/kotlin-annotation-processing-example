package com.example.processor

import com.example.annotations.GenerateDto
import com.example.annotations.Generated
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

/**
 * Annotation processor for generating DTOs.
 *
 * Processes classes annotated with GenerateDto.
 * Creates write and read DTO classes.
 * Generates conversion extension functions.
 */
@SupportedAnnotationTypes("com.example.annotations.GenerateDto")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class DtoProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var filer: Filer

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        filer = processingEnv.filer
    }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val annotatedElements = roundEnv.getElementsAnnotatedWith(GenerateDto::class.java)

        for (element in annotatedElements) {
            if (element.kind != ElementKind.CLASS) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "GenerateDto can only be applied to classes",
                    element
                )
                continue
            }

            val typeElement = element as TypeElement
            processEntity(typeElement)
        }

        return true
    }

    private fun processEntity(typeElement: TypeElement) {
        val annotation = typeElement.getAnnotation(GenerateDto::class.java)
        val packageName = processingEnv.elementUtils.getPackageOf(typeElement).toString()
        val entityName = typeElement.simpleName.toString()

        // Get fields
        val fields = typeElement.enclosedElements
            .filter { it.kind == ElementKind.FIELD }
            .map { it as VariableElement }

        val excludedFields = annotation.exclude.toSet()

        // Generate write DTO
        if (annotation.writeDto) {
            generateWriteDto(packageName, entityName, fields, excludedFields)
        }

        // Generate read DTO
        if (annotation.readDto) {
            generateReadDto(packageName, entityName, fields, excludedFields)
        }

        // Generate extension functions
        generateExtensions(packageName, entityName, fields, excludedFields)
    }

    private fun generateWriteDto(
        packageName: String,
        entityName: String,
        fields: List<VariableElement>,
        excludedFields: Set<String>
    ) {
        val className = "${entityName}WriteDto"

        // Filter fields for write DTO
        val writeFields = fields.filter { field ->
            val fieldName = field.simpleName.toString()
            // Exclude generated fields and explicitly excluded fields
            field.getAnnotation(Generated::class.java) == null && fieldName !in excludedFields
        }

        // Build class
        val classBuilder = TypeSpec.classBuilder(className)
            .addModifiers(KModifier.DATA)

        // Add constructor with parameters
        val constructor = FunSpec.constructorBuilder()

        for (field in writeFields) {
            val fieldName = field.simpleName.toString()
            val fieldType = field.asType().asTypeName()

            constructor.addParameter(fieldName, fieldType)
            classBuilder.addProperty(
                PropertySpec.builder(fieldName, fieldType)
                    .initializer(fieldName)
                    .build()
            )
        }

        classBuilder.primaryConstructor(constructor.build())

        // Write file
        val fileSpec = FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()

        writeFile(packageName, className, fileSpec)
    }

    private fun generateReadDto(
        packageName: String,
        entityName: String,
        fields: List<VariableElement>,
        excludedFields: Set<String>
    ) {
        val className = "${entityName}ReadDto"

        // Filter fields for read DTO
        val readFields = fields.filter { field ->
            val fieldName = field.simpleName.toString()
            fieldName !in excludedFields
        }

        // Build class
        val classBuilder = TypeSpec.classBuilder(className)
            .addModifiers(KModifier.DATA)

        // Add constructor with parameters
        val constructor = FunSpec.constructorBuilder()

        for (field in readFields) {
            val fieldName = field.simpleName.toString()
            val fieldType = field.asType().asTypeName()

            constructor.addParameter(fieldName, fieldType)
            classBuilder.addProperty(
                PropertySpec.builder(fieldName, fieldType)
                    .initializer(fieldName)
                    .build()
            )
        }

        classBuilder.primaryConstructor(constructor.build())

        // Write file
        val fileSpec = FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()

        writeFile(packageName, className, fileSpec)
    }

    private fun generateExtensions(
        packageName: String,
        entityName: String,
        fields: List<VariableElement>,
        excludedFields: Set<String>
    ) {
        val fileName = "${entityName}Extensions"

        val fileBuilder = FileSpec.builder(packageName, fileName)

        // Generate toReadDto extension
        val toReadDto = FunSpec.builder("toReadDto")
            .receiver(ClassName(packageName, entityName))
            .returns(ClassName(packageName, "${entityName}ReadDto"))

        val readFields = fields.filter { it.simpleName.toString() !in excludedFields }
        val readDtoArgs = readFields.joinToString(", ") { field ->
            val fieldName = field.simpleName.toString()
            "$fieldName = this.$fieldName"
        }

        toReadDto.addStatement("return ${entityName}ReadDto($readDtoArgs)")

        fileBuilder.addFunction(toReadDto.build())

        // Generate toEntity extension for write DTO
        val toEntity = FunSpec.builder("toEntity")
            .receiver(ClassName(packageName, "${entityName}WriteDto"))
            .returns(ClassName(packageName, entityName))

        // Add parameters for generated fields
        val generatedFields = fields.filter {
            it.getAnnotation(Generated::class.java) != null
        }

        for (field in generatedFields) {
            val fieldName = field.simpleName.toString()
            val fieldType = field.asType().asTypeName()
            toEntity.addParameter(fieldName, fieldType)
        }

        val entityArgs = fields.filter {
            it.simpleName.toString() !in excludedFields
        }.joinToString(", ") { field ->
            val fieldName = field.simpleName.toString()
            if (field.getAnnotation(Generated::class.java) != null) {
                "$fieldName = $fieldName"
            } else {
                "$fieldName = this.$fieldName"
            }
        }

        toEntity.addStatement("return $entityName($entityArgs)")

        fileBuilder.addFunction(toEntity.build())

        // Write file
        val fileSpec = fileBuilder.build()
        writeFile(packageName, fileName, fileSpec)
    }

    private fun writeFile(packageName: String, className: String, fileSpec: FileSpec) {
        try {
            val kaptGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            val outputDir = kaptGeneratedDir?.let { File(it) } ?: run {
                messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "kapt.kotlin.generated option not available, using filer"
                )
                return
            }

            fileSpec.writeTo(outputDir)
            messager.printMessage(
                Diagnostic.Kind.NOTE,
                "Generated $packageName.$className"
            )
        } catch (e: Exception) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to write $className: ${e.message}"
            )
        }
    }
}

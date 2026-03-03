package com.mokelab.android.cfd.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream

class ComposeFlowDiagramProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Collect functions with @ScreenNode annotation
        val symbols = resolver.getSymbolsWithAnnotation("com.mokelab.android.cfd.ScreenNode")
            .filterIsInstance<KSFunctionDeclaration>()

        val unableToProcess = symbols.filterNot { it.validate() }.toList()

        val nodes = symbols.filter { it.validate() }.toList()
        if (nodes.isNotEmpty()) {
            generateMermaidFile(nodes)
        }

        return unableToProcess
    }

    private fun generateMermaidFile(functions: List<KSFunctionDeclaration>) {
        // build Mermaid
        val mermaidStringBuilder = StringBuilder().apply {
            append("graph TD\n")

            functions.forEach { function ->
                val annotation = function.annotations.find {
                    it.shortName.asString() == "ScreenNode"
                } ?: return@forEach

                // collect an argument
                val displayName = annotation.arguments.find { it.name?.asString() == "displayName" }?.value as? String ?: ""
                val routesTo = (annotation.arguments.find { it.name?.asString() == "routesTo" }?.value as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                // Generate ID
                val packageName = function.packageName.asString()
                val fileName = function.containingFile?.fileName?.removeSuffix(".kt") ?: "Unknown"
                val functionName = function.simpleName.asString()

                // [Package].[File]Kt.[Function]_0
                val screenshotId = "${packageName}.${fileName}Kt.${functionName}_0"
                val imagePath = "./screenshots/${screenshotId}.png"

                // Node definition
                append("    ${functionName}[\"<div><b>$displayName</b></div><img src='$imagePath' width='200'/>\"]\n")

                // Transition definition
                routesTo.forEach { target ->
                    append("    $functionName --> $target\n")
                }
            }
        }

        // Write file
        try {
            val file: OutputStream = codeGenerator.createNewFile(
                dependencies = Dependencies(false, *functions.mapNotNull { it.containingFile }.toTypedArray()),
                packageName = "",
                fileName = "SCREEN_FLOW",
                extensionName = "md"
            )
            file.write(mermaidStringBuilder.toString().toByteArray())
            file.close()
        } catch (e: Exception) {
            logger.error("Failed to write Mermaid file: ${e.message}")
        }
    }
}
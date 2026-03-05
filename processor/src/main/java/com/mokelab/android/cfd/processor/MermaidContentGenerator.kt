package com.mokelab.android.cfd.processor

class MermaidContentGenerator {

    fun generate(fileName: String, nodes: List<ScreenNodeInfo>): String {
        return buildString {
            append("```mermaid\ngraph TD\n")
            nodes.forEach { node ->
                val screenshotId = "${node.packageName}.${fileName}Kt.${node.functionName}_0"
                val imagePath = "./screenshots/${screenshotId}.png"
                append("    ${node.functionName}[\"<div><b>${node.displayName}</b></div><img src='$imagePath' width='200'/>\"]")
                append("\n")
                node.routesTo.forEach { target -> append("    ${node.functionName} --> $target\n") }
            }
            append("```\n")
        }
    }
}

package com.mokelab.android.cfd.processor

import org.junit.Assert.assertEquals
import org.junit.Test

class MermaidContentGeneratorTest {

    private val generator = MermaidContentGenerator()

    @Test
    fun `ノードがないとき空のgraphブロックを生成する`() {
        val result = generator.generate("LoginScreen", emptyList())

        assertEquals("```mermaid\ngraph TD\n```\n", result)
    }

    @Test
    fun `routesToが空のノードのとき矢印なしで生成する`() {
        val node = ScreenNodeInfo(
            functionName = "LoginPreview",
            packageName = "com.example",
            displayName = "Login Screen",
            routesTo = emptyList()
        )

        val result = generator.generate("LoginScreen", listOf(node))

        val expected = "```mermaid\n" +
            "graph TD\n" +
            "    LoginPreview[\"<div><b>Login Screen</b></div>" +
            "<img src='./screenshots/com.example.LoginScreenKt.LoginPreview_0.png' width='200'/>\"]" +
            "\n" +
            "```\n"
        assertEquals(expected, result)
    }

    @Test
    fun `routesToがあるとき矢印を含むgraphを生成する`() {
        val node = ScreenNodeInfo(
            functionName = "LoginPreview",
            packageName = "com.example",
            displayName = "Login Screen",
            routesTo = listOf("HomePreview")
        )

        val result = generator.generate("LoginScreen", listOf(node))

        val expected = "```mermaid\n" +
            "graph TD\n" +
            "    LoginPreview[\"<div><b>Login Screen</b></div>" +
            "<img src='./screenshots/com.example.LoginScreenKt.LoginPreview_0.png' width='200'/>\"]" +
            "\n" +
            "    LoginPreview --> HomePreview\n" +
            "```\n"
        assertEquals(expected, result)
    }

    @Test
    fun `routesToが複数あるとき全ての矢印を生成する`() {
        val node = ScreenNodeInfo(
            functionName = "HomePreview",
            packageName = "com.example",
            displayName = "Home",
            routesTo = listOf("DetailPreview", "SettingsPreview")
        )

        val result = generator.generate("HomeScreen", listOf(node))

        assert(result.contains("    HomePreview --> DetailPreview\n"))
        assert(result.contains("    HomePreview --> SettingsPreview\n"))
    }

    @Test
    fun `複数ノードがあるとき全てのノードと矢印を生成する`() {
        val nodes = listOf(
            ScreenNodeInfo(
                functionName = "LoginPreview",
                packageName = "com.example",
                displayName = "Login",
                routesTo = listOf("HomePreview")
            ),
            ScreenNodeInfo(
                functionName = "HomePreview",
                packageName = "com.example",
                displayName = "Home",
                routesTo = emptyList()
            )
        )

        val result = generator.generate("MyScreen", nodes)

        assert(result.contains("LoginPreview"))
        assert(result.contains("HomePreview"))
        assert(result.contains("LoginPreview --> HomePreview"))
    }

    @Test
    fun `screenshotIdにファイル名とKtサフィックスが含まれる`() {
        val node = ScreenNodeInfo(
            functionName = "FooPreview",
            packageName = "com.example.feature",
            displayName = "Foo",
            routesTo = emptyList()
        )

        val result = generator.generate("FooScreen", listOf(node))

        assert(result.contains("./screenshots/com.example.feature.FooScreenKt.FooPreview_0.png"))
    }
}

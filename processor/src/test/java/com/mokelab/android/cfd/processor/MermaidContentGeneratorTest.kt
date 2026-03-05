package com.mokelab.android.cfd.processor

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class MermaidContentGeneratorTest : StringSpec({

    val generator = MermaidContentGenerator()

    "should generate empty graph block when no nodes" {
        val result = generator.generate("LoginScreen", emptyList())

        result shouldBe "```mermaid\ngraph TD\n```\n"
    }

    "should generate node without arrow when routesTo is empty" {
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
        result shouldBe expected
    }

    "should generate graph with arrow when routesTo is present" {
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
        result shouldBe expected
    }

    "should generate all arrows when multiple routesTo are present" {
        val node = ScreenNodeInfo(
            functionName = "HomePreview",
            packageName = "com.example",
            displayName = "Home",
            routesTo = listOf("DetailPreview", "SettingsPreview")
        )

        val result = generator.generate("HomeScreen", listOf(node))

        result shouldContain "    HomePreview --> DetailPreview\n"
        result shouldContain "    HomePreview --> SettingsPreview\n"
    }

    "should generate all nodes and arrows when multiple nodes are present" {
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

        result shouldContain "LoginPreview"
        result shouldContain "HomePreview"
        result shouldContain "LoginPreview --> HomePreview"
    }

    "screenshotId should contain file name and Kt suffix" {
        val node = ScreenNodeInfo(
            functionName = "FooPreview",
            packageName = "com.example.feature",
            displayName = "Foo",
            routesTo = emptyList()
        )

        val result = generator.generate("FooScreen", listOf(node))

        result shouldContain "./screenshots/com.example.feature.FooScreenKt.FooPreview_0.png"
    }

    "should not contain arrow when routesTo is empty" {
        val node = ScreenNodeInfo(
            functionName = "LoginPreview",
            packageName = "com.example",
            displayName = "Login",
            routesTo = emptyList()
        )

        val result = generator.generate("LoginScreen", listOf(node))

        result shouldNotContain "-->"
    }
})

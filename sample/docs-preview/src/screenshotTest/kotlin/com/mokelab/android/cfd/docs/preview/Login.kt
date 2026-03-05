package com.mokelab.android.cfd.docs.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.mokelab.android.cfd.ScreenNode
import com.mokelab.android.cfd.sample.feature.login.LoginScreen
import com.mokelab.android.cfd.sample.feature.login.RegisterScreen

@PreviewTest
@Preview(showBackground = true)
@ScreenNode(
    displayName = "Login",
    routesTo = ["Register"],
)
@Composable
fun Login() {
    LoginScreen(
        onLoginSuccess = {},
        onNavigateToRegister = {},
    )
}

@PreviewTest
@Preview(showBackground = true)
@ScreenNode(
    displayName = "Register",
    routesTo = ["Login"],
)
@Composable
fun Register() {
    RegisterScreen(
        onRegisterSuccess = {},
        onNavigateToLogin = {},
    )
}
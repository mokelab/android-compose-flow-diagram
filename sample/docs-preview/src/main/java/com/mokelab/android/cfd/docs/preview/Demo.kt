package com.mokelab.android.cfd.docs.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mokelab.android.cfd.sample.feature.login.LoginScreen
import com.mokelab.android.cfd.sample.feature.login.RegisterScreen

@Preview
@Composable
fun PreviewLogin() {
    LoginScreen(
        onLoginSuccess = {},
        onNavigateToRegister = {},
    )
    RegisterScreen(
        onRegisterSuccess = {},
        onNavigateToLogin = {},
    )
}
package com.mokelab.android.cfd.processor

data class ScreenNodeInfo(
    val functionName: String,
    val packageName: String,
    val displayName: String,
    val routesTo: List<String>
)

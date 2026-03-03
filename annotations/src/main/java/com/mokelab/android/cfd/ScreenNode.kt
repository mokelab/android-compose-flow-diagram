package com.mokelab.android.cfd

/**
 * Defines a node in the screen transition diagram.
 *
 * @param displayName The screen name displayed in the diagram (e.g., "Login Screen")
 * @param routesTo List of destination Preview function names (e.g., ["HomePreview"])
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ScreenNode(
    val displayName: String,
    val routesTo: Array<String> = []
)
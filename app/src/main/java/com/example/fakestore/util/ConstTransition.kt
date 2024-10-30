package com.example.fakestore.util

import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.TransformOrigin

object ConstTransition {
    val ENTER_SLIDE_IN_HORIZONTAL = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 500),
    )
    val EXIT_SLIDE_OUT_HORIZONTAL = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = 500)
    )

    val POP_ENTER_SLIDE_IN_HORIZONTAL = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = 500),
    )
    val POP_EXIT_SLIDE_IN_HORIZONTAL = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 500)
    )

    val ENTER_SCALE_IN_CART = scaleIn(
        initialScale = 0.01f,
        animationSpec = tween(durationMillis = 250),
        transformOrigin = TransformOrigin(0.85f, 0.05f)
    )
    val EXIT_SCALE_OUT_CART = scaleOut(
        targetScale = 1.2f,
        animationSpec = tween(durationMillis = 250),
        transformOrigin = TransformOrigin(0.85f, 0.05f)
    )

    val POP_ENTER_SCALE_IN_CART = scaleIn(
        initialScale = 1.2f,
        animationSpec = tween(durationMillis = 300),
        transformOrigin = TransformOrigin(0.85f, 0.05f)
    )
    val POP_EXIT_SCALE_OUT_CART = scaleOut(
        targetScale = 0.01f,
        animationSpec = tween(durationMillis = 300),
        transformOrigin = TransformOrigin(0.85f, 0.05f)
    )
}
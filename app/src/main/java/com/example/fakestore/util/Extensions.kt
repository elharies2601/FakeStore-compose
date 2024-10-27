package com.example.fakestore.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.fakestore.data.model.NetworkResult
import com.kmpalette.palette.graphics.Palette
import retrofit2.HttpException
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

suspend fun <T> safeCallApi(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall.invoke())
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> NetworkResult.Error("Authentication failed")
            403 -> NetworkResult.Error("Access denied")
            404 -> NetworkResult.Error("Resource not found")
            else -> NetworkResult.Error("An error occurred")
        }
    } catch (e: IOException) {
        NetworkResult.Error("Network error")
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "An unexpected error occurred")
    }
}

fun NavController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}

@Composable
internal fun Palette?.paletteBackgroundColor(): State<Color> {
    val defaultBackground = MaterialTheme.colorScheme.background
    return remember(this) {
        derivedStateOf {
            val rgb = this?.dominantSwatch?.rgb
            if (rgb == null) {
                defaultBackground
            } else {
                Color(rgb)
            }
        }
    }
}

@Composable
internal fun Palette?.paletteTextColor(): State<Color> {
    val defaultTitleTextColor = MaterialTheme.colorScheme.onBackground
    return remember(this) {
        derivedStateOf {
            val rgb = this?.dominantSwatch?.bodyTextColor
            if (rgb == null) {
                defaultTitleTextColor
            } else {
                Color(rgb)
            }
        }
    }
}

fun Double.toFormat(): String {
    val nf = NumberFormat.getNumberInstance(Locale.ROOT)
    val formatter = nf as DecimalFormat
    nf.roundingMode = RoundingMode.DOWN
    formatter.applyPattern("#,###,###,###,###")
    return "Rp ${formatter.format(this).replace(",",".")}"
}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }
}

fun String.nameAlias(): String {
    val words = this.trim().split(" ").filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> {
            val word = words[0]
            if (word.length >= 2) {
                word.take(2).uppercase()
            } else {
                word.uppercase()
            }
        }
        words.size > 2 -> {
            words.take(2).joinToString("") { it.first().uppercase() }
        }
        else -> {
            words.joinToString("") { it.first().uppercase() }
        }
    }
}
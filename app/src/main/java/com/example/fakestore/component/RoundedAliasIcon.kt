package com.example.fakestore.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoundedAliasIcon(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    fontSize: TextUnit = 16.sp,
    alias: String = ""
) {
    Box(
        modifier = modifier
            .size(size)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            alias,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview(name = "RoundedAliasIcon")
@Composable
private fun PreviewRoundedAliasIcon() {
    RoundedAliasIcon(alias = "PMEH")
}
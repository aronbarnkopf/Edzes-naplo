package com.workouttracker.ui.view


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

@Composable
fun BottomBar() {
    BottomAppBar(
        actions = {
            Text(
                text = "BottomBar",
                color = Color.White
            )
        },
        containerColor = Color.Red
    )
}

@Composable
@Preview
fun PreviewBottomBar() {
    BottomBar()
}
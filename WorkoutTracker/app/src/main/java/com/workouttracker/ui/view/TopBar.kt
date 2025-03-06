package com.workouttracker.ui.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = "title",
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue)
    )
}

@Composable
@Preview
fun PreviewTopBar() {
    TopBar()
}
package com.workouttracker.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.workouttracker.R
import com.workouttracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onNavigate: (String) -> Unit) {
    Scaffold (
        topBar = { CenterAlignedTopAppBar(title = { Text("LogLift", style = MaterialTheme.typography.headlineMedium) }) }
    ){innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)
            .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onNavigate("timer") }
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightGrayBlue
                )
            ) {
                Box(){
                    Image(
                        painter = painterResource(id = R.drawable.timer2),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                            .alpha(0.4f)
                    )

                    Text("Timer", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onNavigate("workout_log") }
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GrayBlue
                )
            ) {
                Box(){
                    Image(
                        painter = painterResource(id = R.drawable.workout2),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                            .alpha(0.4f)
                    )

                    Text("Tracker", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onNavigate("stats") }
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGrayBlue
                )
            ) {
                Box(){
                    Image(
                        painter = painterResource(id = R.drawable.stats2),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                            .alpha(0.4f)
                    )
                    Text("Statistics", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}

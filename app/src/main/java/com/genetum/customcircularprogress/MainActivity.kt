package com.genetum.customcircularprogress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.circular.progress.CircularProgress
import com.circular.progress.ProgressType
import com.genetum.customcircularprogress.ui.theme.CustomCircularProgressTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomCircularProgressTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            progress += 0.01f
            delay(100)
        }
    }
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(top = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.RadialPulse
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier
                )
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.Wave
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier,
                    color = Color.White
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.Gradient
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier
                )
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.Dotted
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier,
                    color = Color.Black
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.Segmented
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier
                )
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.Bubble
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier,
                    color = Color.Black
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.GlowingRotating
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier
                )
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = progress,
                    type = ProgressType.DualRing
                )
                Text(
                    text = "${(progress*100).toInt()}%",
                    modifier = modifier,
                    color = Color.Black
                )
            }
        }
//        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).weight(1f), horizontalArrangement = Arrangement.Center) {
//            Box(contentAlignment = Alignment.Center) {
//                CircularProgress(
//                    progress = progress,
//                    type = ProgressType.RadialPulse
//                )
//                Text(
//                    text = "${(progress*100).toInt()}%",
//                    modifier = modifier
//                )
//            }
//        }
    }

}
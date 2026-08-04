package com.example.vietaicoach.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.vietaicoach.ui.theme.VietAICoachTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VietAICoachTheme {
                // Survives rotation so the intro does not replay mid-animation.
                var showSplash by rememberSaveable { mutableStateOf(true) }

                Crossfade(targetState = showSplash, label = "splash") { splash ->
                    if (splash) {
                        // Outside the Scaffold so it draws full-bleed under the system bars.
                        SplashScreen(onFinished = { showSplash = false })
                    } else {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            ChatScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}

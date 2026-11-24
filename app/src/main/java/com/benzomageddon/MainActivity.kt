package com.benzomageddon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.benzomageddon.ui.navigation.AppNavHost
import com.benzomageddon.ui.theme.BenzomagedonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BenzomagedonTheme {
                AppNavHost()
            }
        }
    }
}

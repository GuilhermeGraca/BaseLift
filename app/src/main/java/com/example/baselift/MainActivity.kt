package com.example.baselift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.baselift.View.theme.BaseLiftTheme
import com.example.baselift.View.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as BaseLiftApplication).container
        
        setContent {
            BaseLiftTheme {
                AppNavigation(appContainer = appContainer, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

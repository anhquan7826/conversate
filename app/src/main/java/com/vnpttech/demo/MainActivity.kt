package com.vnpttech.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vnpttech.demo.data.AppContainer
import com.vnpttech.demo.data.AppContainerImpl
import com.vnpttech.demo.navigation.NavigationView
import com.vnpttech.demo.ui.theme.DemoInternshipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container: AppContainer = AppContainerImpl()
        setContent {
            DemoInternshipTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NavigationView(container)
                }
            }
        }
    }
}

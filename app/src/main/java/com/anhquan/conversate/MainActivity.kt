package com.anhquan.conversate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.anhquan.conversate.data.AppContainer
import com.anhquan.conversate.data.AppContainerImpl
import com.anhquan.conversate.navigation.NavigationView
import com.anhquan.conversate.ui.theme.DemoInternshipTheme

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
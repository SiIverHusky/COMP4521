package edu.hkust.qust

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                UserProfileScreen()
            }
        }
    }
}

    @Composable
    fun UserProfileScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Profile Image Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text("Profile", fontSize = 16.sp)
            }

            // Name and Class
            Text("Name", fontSize = 24.sp)
            Text("Class", fontSize = 18.sp)

            // Level Progress Bar
            Text("Level 5")
            LinearProgressIndicator(progress = 0.75f, modifier = Modifier.fillMaxWidth())

            // Other Progress Bars
            Text("20")
            LinearProgressIndicator(progress = 0.2f, modifier = Modifier.fillMaxWidth())

            Text("15")
            LinearProgressIndicator(progress = 0.15f, modifier = Modifier.fillMaxWidth())

            Text("100%")
            LinearProgressIndicator(progress = 1f, modifier = Modifier.fillMaxWidth())

            // Current Task
            Text("Current Task")
            Text("2/5", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Upcoming Tasks
            Text("Upcoming")
            Text("2 days")
            Spacer(modifier = Modifier.height(4.dp))
            Text("1 week")
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Navigation Bar
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White
            ) {
                val items = listOf("Home", "Add", "Users")
                val icons = listOf("🏠", "➕", "👥")

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { icons[index] },
                        label = { item },
                        selected = false,
                        onClick = { null }

                    )
                }
            }
        }
    }





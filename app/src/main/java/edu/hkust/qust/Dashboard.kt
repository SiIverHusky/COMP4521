package edu.hkust.qust

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@SuppressLint("RestrictedApi")
class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                bottomBar = { BottomNavigationBar() }
            ) { innerPadding ->
                // Main content goes here
                UserProfileScreen(Modifier.padding(innerPadding))
            }
        }
    }
}



@Composable
fun UserProfileScreen(modifier: Modifier) {
    Spacer(Modifier.padding(vertical = 10.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, top = 40.dp, end = 25.dp, bottom = 10.dp),
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
        Text("Name", Modifier.padding(top = 10.dp, bottom = 5.dp), fontSize = 24.sp)
        Text("Class", Modifier.padding(top = 15.dp, bottom = 5.dp), fontSize = 18.sp)

        // Level Progress Bar
        Text("Level 5", Modifier.padding(top = 15.dp, bottom = 3.dp))
        LinearProgressIndicator(
        progress = { 0.75f },
        modifier = Modifier.fillMaxWidth().padding(top = 3.dp, bottom = 10.dp)
        )

        // Other Progress Bars
        Text("20")
        LinearProgressIndicator(
        progress = { 0.2f },
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 10.dp)
        )

        Text("15")
        LinearProgressIndicator(
        progress = { 0.15f },
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 10.dp)
        )

        Text("100%")
        LinearProgressIndicator(
        progress = { 1f },
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 10.dp)
        )

        // Current Task
        Text("Current Task", Modifier.padding(top = 35.dp, bottom = 5.dp), fontSize = 25.sp)
        Text("2/5", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming Tasks
        Text("Upcoming", fontSize = 25.sp)
        Text("2 days", Modifier.padding(start = 5.dp, top = 10.dp))
        //Spacer(modifier = Modifier.height(4.dp))
        Text("1 week", Modifier.padding(start = 5.dp, top = 10.dp))
        //Spacer(modifier = Modifier.height(8.dp))

        val selectedNavigationIndex = rememberSaveable {
            mutableIntStateOf(0)
        }

        }
    }

@Composable
fun BottomNavigationBar() {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar(
        modifier = Modifier.fillMaxWidth().padding(top = 0.dp),
        containerColor = Color.White
    ) {
        val navigationItems = listOf(
            NavigationItem(title = "Home", icon = Icons.Default.Home),
            NavigationItem(title = "Menu", icon = Icons.Default.Menu),
            NavigationItem(title = "Add", icon = Icons.Default.Add),
            NavigationItem(title = "Person", icon = Icons.Default.Person)
        )

        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(
                    item.title,
                    color = if (index == selectedNavigationIndex.intValue)
                        Color.Black
                    else Color.Gray
                ) },
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    //navController.navigate(item.route)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}


data class NavigationItem(
    val title: String,
    val icon: ImageVector,
)





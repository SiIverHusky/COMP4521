package edu.hkust.qust

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class QuestLog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskAndQuestScreen()
        }
    }
}

@Composable
fun TaskAndQuestScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Tasks Section
        Text(text = "Tasks", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Example of task items
        for (i in 1..3) {
            TaskItem(taskName = "Task $i")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quests Section
        Text(text = "Quests", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Example of quest items
        for (i in 1..3) {
            QuestItem(questName = "Quest $i")
        }

        Spacer(modifier = Modifier.height(16.dp))


    }
}

@Composable
fun TaskItem(taskName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = taskName)
            Text(text = "✓", modifier = Modifier.clickable { /* Handle check */ })
        }
    }
}

@Composable
fun QuestItem(questName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = questName)
            Row {
                Text(text = "✓", modifier = Modifier.clickable { /* Handle check */ })
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "✗", modifier = Modifier.clickable { /* Handle uncheck */ })
            }
        }
    }
}


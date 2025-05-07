package edu.hkust.qust

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NewQuest: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewTaskScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen() {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "New Task", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Name Input
        Text(text = "Name")
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quantity Input
        Text(text = "Quantity")
        TextField(
            value = quantity,
            onValueChange = { quantity = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Duration Input
        Text(text = "Duration")
        TextField(
            value = duration,
            onValueChange = { duration = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Deadline Input
        Text(text = "Deadline")
        TextField(
            value = deadline,
            onValueChange = { deadline = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Type Dropdown
        Text(text = "Type")
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { /* handle dropdown expansion */ }
        ) {
            TextField(
                value = selectedType,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            )
            // Add dropdown items here
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}
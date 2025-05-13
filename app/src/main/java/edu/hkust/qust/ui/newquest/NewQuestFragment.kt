package edu.hkust.qust.ui.newquest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.functions
import edu.hkust.qust.databinding.FragmentNewquestBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar


class NewQuestFragment : Fragment() {

    private var _binding: FragmentNewquestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
//            val testEmail = "haris@gmail.com"
//            val testPassword = "testing"
//            auth.signInWithEmailAndPassword(testEmail, testPassword)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d("NewQuestFragment", "Sign-in successful")
//                    } else {
//                        Log.e("NewQuestFragment", "Sign-in failed: ${task.exception?.message}")
//                    }
//                }
        } else {
            Log.d("NewQuestFragment", "User is already signed in: ${currentUser.email}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newQuestViewModel =
            ViewModelProvider(this).get(NewQuestViewModel::class.java)

        _binding = FragmentNewquestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        return ComposeView(requireContext()).apply {
            setContent {
                if(isUserLoggedIn(requireContext())) {
                    Log.d("NewQuestFragment", "Current User: ${Firebase.auth.currentUser?.toString()}")
                    NewTaskScreen(newQuestViewModel)
                }else{
                    LoginPrompt()
                }

            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDial(
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimePicker(
                state = timePickerState,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = onDismiss) {
                    Text("Dismiss")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                    Text("Confirm")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun formatDeadline(dateMillis: Long?, time: Pair<Int, Int>?): String {
    if (dateMillis == null && time == null) return ""
    val calendar = Calendar.getInstance()
    dateMillis?.let { calendar.timeInMillis = it }
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())
    val year = calendar.get(Calendar.YEAR)
    val hour = time?.first ?: 0
    val minute = time?.second ?: 0
    return String.format("%02d %s. %d - %02d:%02d", day, month, year, hour, minute)
}

// Firebase Functions

fun convertStringToTimestamp(dateString: String): Long? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd MMM. yyyy - HH:mm")
        val localDateTime = LocalDateTime.parse(dateString, formatter)
        localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


private fun createQuest(
    questName: String,
    questDescription: String,
    type: String,
    deadline: String? = null,
    duration: String? = null,
    quantity: String? = null
): Task<String> {
    // Create the arguments to the callable function.
    val data = hashMapOf<String, Any>(
        "questNameString" to questName,
        "questDescriptionString" to questDescription
    ).apply {
        when (type) {
            "Quantity" -> put("questDescriptionString", "Quantity task of $quantity, with description: $questDescription")
            "Duration" -> put("questDescriptionString", "Duration task of $duration, with description: $questDescription")
            "Deadline" -> {
                put("deadlineTimestamp", convertStringToTimestamp(deadline.toString()) ?: 0L)
            }
        }
    }

    // Call the Firebase function and handle the result.
    return Firebase.functions("asia-east2")
        .getHttpsCallable("createQuest")
        .call(data)
        .continueWith { task ->
            // This continuation runs on either success or failure.
            val result = task.result?.data as String
            result
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(newQuestViewModel: NewQuestViewModel) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var durationH by remember { mutableStateOf("") }
    var durationM by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("None") }
    var description by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val types = listOf("None", "Quantity", "Duration", "Deadline")

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimeDial by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize())
    {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Enter Quest Details", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Name Input
            Text(text = "Name")
            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity Input
            if (selectedType == "Quantity") {
                Text(text = "Quantity")
                TextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = quantity.toIntOrNull() == null || quantity.toInt() < 0
                )
            }

            // Duration Input
            if (selectedType == "Duration") {
                Text(text = "Duration")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hours",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    TextField(
                        value = durationH,
                        onValueChange = {
                            durationH = it
                        },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = durationH.toIntOrNull() == null || durationH.toInt() < 0
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Minutes",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    TextField(
                        value = durationM,
                        onValueChange = {
                            durationM = it
                        },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = durationM.toIntOrNull() == null || durationM.toInt() < 0 || durationM.toInt() > 59
                    )
                }
            }


            // Deadline Input
            if (selectedType == "Deadline") {
                Text(text = "Deadline")
                Row {
                    TextField(
                        value = deadline,
                        onValueChange = { /* No-op since this is read-only */ },
                        readOnly = true,
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        label = { Text("Deadline") },
                        placeholder = { Text("Select a deadline") },
                        singleLine = true
                    )
                    Button(onClick = {showDatePicker = true}, modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .align(Alignment.CenterVertically)) {Text("+")}
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Type Dropdown
            Text(text = "Type")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedType,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                )
                // Add dropdown items here
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    types.forEach {type ->
                        DropdownMenuItem(
                            text = { Text(text = type) },
                            onClick = {
                                // Handle item click
                                selectedType = type
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(text = "Description")
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                singleLine = false,
                maxLines = 3
            )
        }
        FloatingActionButton(
            onClick = {
                // Input Validation
                when (selectedType) {
                    "None" -> {
                        if (name.isBlank() || description.isBlank()) {
                            // Show error (e.g., Toast or Snackbar)
                            return@FloatingActionButton
                        }
                    }
                    "Quantity" -> {
                        if (name.isBlank() || quantity.isBlank() || description.isBlank()) {
                            // Show error (e.g., Toast or Snackbar)
                            return@FloatingActionButton
                        }
                    }
                    "Duration" -> {
                        if (name.isBlank() || durationH.isBlank() || durationM.isBlank() || description.isBlank()) {
                            // Show error (e.g., Toast or Snackbar)
                            return@FloatingActionButton
                        }
                    }
                    "Deadline" -> {
                        if (name.isBlank() || deadline.isBlank() || description.isBlank()) {
                            // Show error (e.g., Toast or Snackbar)
                            return@FloatingActionButton
                        }
                    }
                    else -> {
                        return@FloatingActionButton
                    }
                }

                createQuest(
                    questName = name,
                    questDescription = description,
                    type = selectedType,
                    deadline = if (selectedType == "Deadline") deadline else null,
                    duration = if (selectedType == "Duration") "${durationH}h ${durationM}m" else null,
                    quantity = if (selectedType == "Quantity") quantity else null,
                ).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                            Log.d("NewQuestFragment", "Error $code: $details")
                        }
                    } else {
                        Log.d("NewQuestFragment", "Created Quest!")
                        selectedType = "None"
                        name = ""
                        description = ""
                        quantity = ""
                        durationH = ""
                        durationM = ""
                        deadline = ""
                    }
                }
            },
            modifier = Modifier
                .padding(PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 64.dp // Specify a different bottom padding
                ))
                .align(Alignment.BottomEnd)
        ) { Text("+") }
    }

    if (showDatePicker) {
        DatePicker(
            onDateSelected = { dateMillis ->
                selectedDate = dateMillis
                showDatePicker = false
                showTimeDial = true
            },
            onDismiss = {
                showDatePicker = false
                showTimeDial = true
            }
        )
    }

    if (showTimeDial) {
        TimeDial(
            onConfirm = { hour, minute ->
                selectedTime = hour to minute
                showTimeDial = false
                deadline = formatDeadline(selectedDate, selectedTime)
            },
            onDismiss = {
                showTimeDial = false
                deadline = formatDeadline(selectedDate, selectedTime)
            }
        )
    }
}

@Composable
fun LoginPrompt() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Please log in to view this content.", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))


    }
}

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false) // Default is false
}
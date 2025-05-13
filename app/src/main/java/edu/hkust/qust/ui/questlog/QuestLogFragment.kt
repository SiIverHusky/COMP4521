package edu.hkust.qust.ui.questlog

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import edu.hkust.qust.databinding.FragmentQuestlogBinding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import com.google.firebase.firestore.FieldValue
import kotlin.collections.minusAssign
import kotlin.compareTo
import kotlin.text.get


class QuestLogFragment : Fragment() {

    private var _binding: FragmentQuestlogBinding? = null

    private lateinit var auth: FirebaseAuth;
    private lateinit var firestore: FirebaseFirestore
    val db = FirebaseFirestore.getInstance()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()


        val questLogViewModel =
            ViewModelProvider(this).get(QuestLogViewModel::class.java)

        _binding = FragmentQuestlogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        return ComposeView(requireContext()).apply {
            setContent {
                if(isUserLoggedIn(requireContext())) {
                    TaskAndQuestScreen(questLogViewModel, firestore, auth)
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


@Composable
fun TaskAndQuestScreen(
    questLogViewModel: QuestLogViewModel,
    firestore: FirebaseFirestore,
    auth: FirebaseAuth
) {
    var currentQuests by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var completedQuests by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("questLog")
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        Log.w("QuestLog", "Listen failed.", exception)
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        val current = mutableListOf<Map<String, Any>>()
                        val completed = mutableListOf<Map<String, Any>>()

                        for (document in snapshots) {
                            val quest = document.data.toMutableMap()
                            quest["questId"] = document.id // Add document name to the quest map
                            val status = quest["questStatusString"] as? String ?: "Unknown"
                            val questUserId = quest["userIdString"] as? String
                            val partyIdArray = quest["partyIdArray"] as? List<*>

                            // Check if the quest is a CoopQuestItem
                            val isCoopQuest = questUserId != userId && partyIdArray?.contains(userId) == true

                            if (status == "Completed" && (isCoopQuest || userId == questUserId)) {
                                Log.d("QuestLog", "Completed quest: $quest")
                                completed.add(quest)
                            } else if (status != "Canceled" && (isCoopQuest || userId == questUserId)) {
                                Log.d("QuestLog", "Current quest: $quest")
                                current.add(quest)
                            }
                        }
                        Log.d("QuestLog", "Current quests: $current")
                        Log.d("QuestLog", "Completed quests: $completed")
                        currentQuests = current
                        completedQuests = completed
                    }
                }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Current Quests Section
        Text(text = "Current Quests", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.weight(2f)) {
            items(currentQuests) { quest ->
                val questUserId = quest["userIdString"] as? String
                if (questUserId == auth.currentUser?.uid) {
                    QuestItem(quest, firestore, auth)
                } else {
                    CoopQuestItem(quest, firestore, auth)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Completed Quests Section
        Text(text = "Completed Quests", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(completedQuests) { quest ->
                val questUserId = quest["userIdString"] as? String
                if (questUserId == auth.currentUser?.uid) {
                    QuestItem(quest, firestore, auth)
                } else {
                    CoopQuestItem(quest, firestore, auth)
                }
            }
        }
    }
}

@Composable
fun QuestItem(quest: Map<String, Any>,
              firestore: FirebaseFirestore,
              auth: FirebaseAuth) {
    val questName = quest["questNameString"] as? String ?: "Unknown Quest"
    var questDescription = quest["questDescriptionString"] as? String ?: "No Description"
    val deadlineTimestamp = quest["deadlineTimestamp"] as? Long
    val questId = quest["questId"] as? String ?: "Unknown ID"
    val questStatus = quest["questStatusString"] as? String ?: "Unknown Status"
    val progress = quest["progressString"] as? String ?: ""
    val partyPendingInvitationArray = quest["partyPendingInvitationArray"] as? List<*>

    val currentQuantity = quest["currentQuantity"] as? Int ?: 0
    val totalQuantity = Regex("""Quantity task of (\d+),""").find(questDescription)?.groupValues?.get(1)?.toIntOrNull()
    var remainingTime by remember { mutableStateOf(quest["remainingTime"] as? Long ?: 0L) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Determine quest type and extract relevant data
    val questType: String
    var additionalInfo: String? = null

    when {
        questDescription.startsWith("Quantity task of") -> {
            questType = "Quantity"
            val regex = Regex("""Quantity task of (\d+), with description: (.+)""")
            val matchResult = regex.find(questDescription)
            if (matchResult != null) {
                val (total, description) = matchResult.destructured
                questDescription = description
                val current = quest["currentQuantity"] as? Int ?: 0
                additionalInfo = "$current / $total"
            }
        }
        questDescription.startsWith("Duration task of") -> {
            questType = "Duration"
            val regex = Regex("""Duration task of (\d+h \d+m), with description: (.+)""")
            val matchResult = regex.find(questDescription)
            if (matchResult != null) {
                val (duration, description) = matchResult.destructured
                questDescription = description
                additionalInfo = formatTime(duration)
            }
        }
        deadlineTimestamp != null -> {
            questType = "Deadline"
            additionalInfo = formatDeadline(deadlineTimestamp)
        }
        else -> {
            questType = "None"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = questName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = questDescription, style = MaterialTheme.typography.bodyMedium)
                additionalInfo?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (questStatus != "Completed")
                {
                    IconButton(
                        onClick = {
                            firestore.collection("questLog").document(questId)
                                .update("questStatusString", "Canceled")
                                .addOnSuccessListener {
                                    Log.d("QuestItem", "Quest successfully canceled.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("QuestItem", "Error canceling quest", e)
                                }
                        },
                        modifier = Modifier
                            .size(48.dp) // Square button
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    if (partyPendingInvitationArray.isNullOrEmpty()) {
                        IconButton(
                            onClick = {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    // Step 1: Retrieve all users
                                    firestore.collection("users")
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            val allUserIds = querySnapshot.documents.mapNotNull { it.id }

                                            // Step 2: Filter out the current user
                                            val otherUserIds = allUserIds.filter { it != userId }

                                            // Step 3: Update the quest document
                                            firestore.collection("questLog").document(questId)
                                                .update("partyPendingInvitationArray", FieldValue.arrayUnion(*otherUserIds.toTypedArray()))
                                                .addOnSuccessListener {
                                                    Log.d("QuestItem", "All users (except current user) added to partyPendingInvitationArray.")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("QuestItem", "Error adding users to partyPendingInvitationArray", e)
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("QuestItem", "Error retrieving users", e)
                                        }
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Join Party",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            when (questType) {
                                "None", "Deadline", "Duration", "Quantity" -> {
                                    firestore.collection("questLog").document(questId)
                                        .update("questStatusString", "Completed")
                                        .addOnSuccessListener {
                                            Log.d("QuestItem", "Quest marked as complete.")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("QuestItem", "Error completing quest", e)
                                        }
                                }
//                            "Duration" -> {
//                                if (isTimerRunning) {
//                                    isTimerRunning = false
//                                    firestore.collection("questLog").document(questId)
//                                        .update("remainingTime", remainingTime)
//                                } else {
//                                    isTimerRunning = true
//                                    firestore.collection("questLog").document(questId)
//                                        .update("remainingTime", remainingTime)
//                                }
//                            }
//                            "Quantity" -> {
//                                if (currentQuantity < totalQuantity!!) {
//                                    firestore.collection("questLog").document(questId)
//                                        .update("currentQuantity", currentQuantity + 1)
//                                        .addOnSuccessListener {
//                                            Log.d("QuestItem", "Quantity incremented.")
//                                            if (currentQuantity + 1 == totalQuantity) {
//                                                firestore.collection("questLog").document(questId)
//                                                    .update("questStatusString", "Completed")
//                                            }
//                                        }
//                                        .addOnFailureListener { e ->
//                                            Log.e("QuestItem", "Error incrementing quantity", e)
//                                        }
//                                }
//                            }
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Complete",
                            tint = MaterialTheme.colorScheme.primary
                        )
//                        Text(
//                            text = when (questType) {
//                                "None", "Deadline", "Quantity", "Duration" -> "Complete"
////                            "Quantity" -> "Increment"
////                            "Duration" -> if (isTimerRunning) "Stop" else "Start"
//                                else -> "Action"
//                            }
//                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (remainingTime > 0) {
                kotlinx.coroutines.delay(1000L)
                remainingTime -= 1000L
                firestore.collection("questLog").document(questId)
                    .update("remainingTime", remainingTime)
                if (remainingTime <= 0) {
                    isTimerRunning = false
                    firestore.collection("questLog").document(questId)
                        .update("questStatusString", "Completed")
                }
            }
        }
    }
}

@Composable
fun CoopQuestItem(
    quest: Map<String, Any>,
    firestore: FirebaseFirestore,
    auth: FirebaseAuth
) {
    val questName = quest["questNameString"] as? String ?: "Unknown Quest"
    val questDescription = quest["questDescriptionString"] as? String ?: "No Description"
    val questId = quest["questId"] as? String ?: "Unknown ID"
    val userId = quest["userIdString"] as? String ?: "Unknown User"
    val questStatus = quest["questStatusString"] as? String ?: "Unknown Status"

    var username by remember { mutableStateOf("Loading...") }

    // Fetch the username from Firestore
    LaunchedEffect(userId) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                username = document.getString("usernameString") ?: "Unknown User"
            }
            .addOnFailureListener {
                username = "Error Loading User"
            }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(150.dp) // Adjust height as needed
        ) {
            // Title on top
            Text(
                text = questName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.TopStart)
            )

            // Description in the middle
            Text(
                text = questDescription,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // Creator username on the bottom right
            Text(
                text = "u/$username",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            // Delete button on the right
            if (questStatus != "Completed")
            {
                IconButton(
                    onClick = {
                        val currentUserId = auth.currentUser?.uid
                        if (currentUserId != null) {
                            firestore.collection("questLog").document(questId)
                                .update(
//                                mapOf(
//                                    "partyIdArray" to FieldValue.arrayRemove(currentUserId), // Remove user from array
//                                    "partyUsernameMap.$currentUserId" to FieldValue.delete() // Remove user from map
//                                )
                                    "partyIdArray", FieldValue.arrayRemove(currentUserId), // Remove user from array
                                )
                                .addOnSuccessListener {
                                    Log.d("CoopQuestItem", "User removed from partyIdArray and partyUsernameMap.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CoopQuestItem", "Error removing user from partyIdArray and partyUsernameMap", e)
                                }
                        }
                    },
                    modifier = Modifier.size(48.dp).align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// Helper function to format remaining time (hh:mm:ss)
fun formatTime(duration: String): String {
    val regex = Regex("""(\d+)h (\d+)m""")
    val matchResult = regex.find(duration)
    return if (matchResult != null) {
        val (hours, minutes) = matchResult.destructured
        String.format("%02d:%02d:00", hours.toInt(), minutes.toInt())
    } else {
        "00:00:00" // Default if parsing fails
    }
}

// Helper function to format deadline (yyyy-MM-dd HH:mm)
fun formatDeadline(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return format.format(date)
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
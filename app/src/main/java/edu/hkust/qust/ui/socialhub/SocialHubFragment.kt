package edu.hkust.qust.ui.socialhub

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.hkust.qust.R
import edu.hkust.qust.databinding.FragmentDashboardBinding
import edu.hkust.qust.databinding.FragmentNewquestBinding
import edu.hkust.qust.databinding.FragmentSocialhubBinding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter

import kotlin.text.get


class SocialHubFragment : Fragment() {

    private var _binding: FragmentSocialhubBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth;
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val socialHubViewModel =
            ViewModelProvider(this).get(SocialHubViewModel::class.java)

        _binding = FragmentSocialhubBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}


        return ComposeView(requireContext()).apply {
            setContent {
                if(isUserLoggedIn(requireContext())) {
                    SocialHubScreen(firestore, auth)
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
fun SocialHubScreen(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth
) {
    var coopQuests by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("SocialHub", "User is not logged in.")
            return@LaunchedEffect
        }

        firestore.collection("questLog")
            .firestore.collection("questLog")
            .whereArrayContains("partyPendingInvitationArray", userId)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    Log.w("SocialHub", "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val filteredQuests = mutableListOf<Map<String, Any>>()

                    for (document in snapshots) {
                        val quest = document.data.toMutableMap()
                        quest["questId"] = document.id
                        val status = quest["questStatusString"] as? String ?: ""

                        // Filter quests with valid statuses
                        if (status != "Canceled" && status != "Completed") {
                            filteredQuests.add(quest)
                        }
                    }

                    // Update the state
                    coopQuests = filteredQuests
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Social Hub", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(coopQuests) { quest ->
                CoopQuestItem(quest, firestore, auth)
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
            .padding(vertical = 4.dp)
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

            // Creator on the bottom left
            Text(
                text = "u/$username",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.BottomStart)
            )

            // Buttons on the right
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
            ) {
                IconButton(
                    onClick = {
                        val currentUserId = auth.currentUser?.uid
                        if (currentUserId != null) {
                            firestore.collection("questLog").document(questId)
                                .update("partyPendingInvitationArray", FieldValue.arrayRemove(currentUserId))
                                .addOnSuccessListener {
                                    Log.d("CoopQuestItem", "User removed from partyPendingInvitationArray.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CoopQuestItem", "Error removing user from partyPendingInvitationArray", e)
                                }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                IconButton(
                    onClick = {
                        val currentUserId = auth.currentUser?.uid
                        if (currentUserId != null) {
                            firestore.collection("users").document(currentUserId).get()
                                .addOnSuccessListener { userDocument ->
                                    val username = userDocument.getString("usernameString") ?: "Unknown User"

                                    firestore.collection("questLog").document(questId)
                                        .update(
                                            "partyPendingInvitationArray", FieldValue.arrayRemove(currentUserId),
                                            "partyIdArray", FieldValue.arrayUnion(currentUserId),
                                            "partyUsernameMap.$currentUserId", username
                                        )
                                        .addOnSuccessListener {
                                            Log.d("CoopQuestItem", "User accepted the quest invitation.")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("CoopQuestItem", "Error updating quest document", e)
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CoopQuestItem", "Error retrieving user document", e)
                                }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
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
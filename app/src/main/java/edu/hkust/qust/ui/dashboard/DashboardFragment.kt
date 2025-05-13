package edu.hkust.qust.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import edu.hkust.qust.R
import edu.hkust.qust.databinding.FragmentDashboardBinding
import kotlinx.coroutines.delay
import kotlin.collections.get
import kotlin.div
import kotlin.text.get
import kotlin.text.toInt


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var isAnimating by mutableStateOf(true)

    private lateinit var auth: FirebaseAuth;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth


        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}


        return ComposeView(requireContext()).apply {
            setContent {
                //SpriteAnimation(context = requireContext()) // Pass context
                if(isUserLoggedIn(requireContext())){
                    val username = getUsername(requireContext())
                    UserProfileScreen(dashboardViewModel, requireContext(), isAnimating, viewLifecycleOwner, username)
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

    override fun onPause() {
        super.onPause()
        isAnimating = false // Stop animation when paused
    }

    override fun onResume() {
        super.onResume()
        isAnimating = true // Resume animation when resumed
    }
}

@Composable
fun UserProfileScreen(
    dashboardViewModel: DashboardViewModel,
    requireContext: Context,
    isAnimating: Boolean,
    viewLifecycleOwner: LifecycleOwner,
    usernameStorage: String?
) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var username by remember { mutableStateOf(usernameStorage ?: "") }
    var accountLevel by remember { mutableStateOf(0) }
    var experiencePoints by remember { mutableStateOf(0) }
    var health by remember { mutableStateOf(0) }
    var strength by remember { mutableStateOf(0) }
    var intelligence by remember { mutableStateOf(0) }

    // Add Firestore listener for real-time updates
    LaunchedEffect(auth.currentUser) {
        val currentUser = auth.currentUser
        Log.d("UserProfileScreen", "Current user: $currentUser")
        if (currentUser != null) {
            val userDocRef = db.collection("users").document(currentUser.uid)
            userDocRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("UserProfileScreen", "Error listening to updates: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("UserProfileScreen", "Document data: ${snapshot.data}")
                    username = snapshot.getString("usernameString") ?: ""
                    accountLevel = snapshot.getLong("accountLevelNumber")?.toInt() ?: 0
                    experiencePoints = snapshot.getLong("experiencePointNumber")?.toInt() ?: 0
                    health = snapshot.getLong("healthNumber")?.toInt() ?: 0
                    strength = snapshot.getLong("strengthNumber")?.toInt() ?: 0
                    intelligence = snapshot.getLong("intelligenceNumber")?.toInt() ?: 0
                } else {
                    Log.d("UserProfileScreen", "Document does not exist")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, top = 40.dp, end = 25.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Profile Image Placeholder
        SpriteAnimation(requireContext, isAnimating)

        // Name and Class
        Text(username, Modifier.padding(top = 10.dp, bottom = 5.dp), fontSize = 24.sp)
        Text("Character class: Knight", Modifier.padding(top = 15.dp, bottom = 5.dp), fontSize = 18.sp)

        // Level Progress Bar
        Text("Lv$accountLevel", Modifier.padding(top = 15.dp, bottom = 3.dp))
        LinearProgressIndicator(
            progress = experiencePoints / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp, bottom = 10.dp)
        )

        // Other Progress Bars
        Text("Strength: $strength")
        LinearProgressIndicator(
            progress = strength / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 10.dp)
        )

        Text("Intelligence: $intelligence")
        LinearProgressIndicator(
            progress = intelligence / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 10.dp)
        )

        Text("HP: $health")
        LinearProgressIndicator(
            progress = health / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 10.dp)
        )

        // Current Task
//        Column {
//            Text("Current Task", Modifier.padding(top = 35.dp, bottom = 5.dp), fontSize = 25.sp)
//            Text("2/5", fontSize = 20.sp)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Upcoming Tasks
//        Column {
//            Text("Upcoming", fontSize = 25.sp)
//            Text("2 days", Modifier.padding(start = 5.dp, top = 10.dp))
//            Text("1 week", Modifier.padding(start = 5.dp, top = 10.dp))
//        }
    }
}

@Composable
fun SpriteAnimation(context: Context, isAnimating: Boolean) {
    val frameCount = 4 // Total number of frames
    val frameDuration = 100L // Duration for each frame in milliseconds

    // Load bitmaps for each frame
    val bitmaps = remember {
        List(frameCount) { index ->
            BitmapFactory.decodeResource(context.resources, getResourceId(index)).asImageBitmap()
        }
    }

    var currentFrame by remember { mutableStateOf(0) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            while (true) {
                delay(frameDuration)
                currentFrame = (currentFrame + 1) % frameCount
            }
        }
    }

    Image(bitmap = bitmaps[currentFrame], contentDescription = null, modifier = Modifier.size(100.dp))
}

private fun getResourceId(index: Int): Int {
    return when (index) {
        0 -> R.drawable.knight0
        1 -> R.drawable.knight1
        2 -> R.drawable.knight2
        3 -> R.drawable.knight3
        else -> R.drawable.knight0 // Default case
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


fun getUsername(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("username", null) // Default is null
}





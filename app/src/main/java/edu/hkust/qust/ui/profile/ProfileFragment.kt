package edu.hkust.qust.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import edu.hkust.qust.databinding.FragmentProfileBinding
import kotlin.collections.get
import kotlin.text.get
import kotlin.text.toInt


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth

        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        isUserLoggedIn(requireContext())
        return ComposeView(requireContext()).apply {
            setContent {
                ProfileApp(profileViewModel, requireContext())
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val db = Firebase.firestore
            val uid = currentUser.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        UserDataStore.username = document.getString("usernameString")
                        UserDataStore.accountLevel = document.getLong("accountLevelNumber")?.toInt()
                        UserDataStore.experiencePoints = document.getLong("experiencePointNumber")?.toInt()
                        UserDataStore.health = document.getLong("healthNumber")?.toInt()
                        UserDataStore.strength = document.getLong("strengthNumber")?.toInt()
                        UserDataStore.intelligence = document.getLong("intelligenceNumber")?.toInt()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileFragment", "Error getting document: ", exception)
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.signOut()
    }


}

object UserDataStore {
    var username: String? = null
    var accountLevel: Int? = null
    var experiencePoints: Int? = null
    var health: Int? = null
    var strength: Int? = null
    var intelligence: Int? = null
}

private fun clearUserData() {
    UserDataStore.username = null
    UserDataStore.accountLevel = null
    UserDataStore.experiencePoints = null
    UserDataStore.health = null
    UserDataStore.strength = null
    UserDataStore.intelligence = null
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileApp(profileViewModel: ProfileViewModel, requireContext: Context){
    var isLoggedIn by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginError by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSignUpError by remember { mutableStateOf(false) }



    isLoggedIn = isUserLoggedIn(requireContext)

    if (isLoggedIn) {
        username = UserDataStore.username.toString()
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)){
        if(isLoggedIn){
            Text("Welcome, ${username}", fontSize = 24.sp)
            // Add more user profile details here

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    // Trigger a recomposition to show the LoginScreen
                    saveLoginStatus(requireContext, false)
                    isLoggedIn = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }else{
            Text(if (isSignUpMode) "Sign up" else "Login", fontSize = 24.sp)

            if (isSignUpMode)
            {
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            if (isSignUpMode)
            {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            if (isLoginError) {
                Text("Invalid email or password", color = MaterialTheme.colorScheme.error)
            }

            if (isSignUpError) {
                Text("Invalid sign up details", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isSignUpMode)
            {
                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            isLoginError = true
                            return@Button
                        }
                        val auth = Firebase.auth
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    isLoginError = true
                                }else{
                                    isLoggedIn = true

                                    // Save logged-in state
                                    saveLoginStatus(requireContext, true)
                                    val user = auth.currentUser
                                    Log.d("ProfileFragment", "User: ${user?.email}")

                                    // Fetch user data from Firestore
                                    val db = Firebase.firestore
                                    val uid = user?.uid
                                    db.collection("users").document(uid.toString()).get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                var userData = document.data
                                                username = userData?.get("usernameString") as String
                                                UserDataStore.username = document.getString("usernameString")
                                                UserDataStore.accountLevel = document.getLong("accountLevelNumber")?.toInt()
                                                UserDataStore.experiencePoints = document.getLong("experiencePointNumber")?.toInt()
                                                UserDataStore.health = document.getLong("healthNumber")?.toInt()
                                                UserDataStore.strength = document.getLong("strengthNumber")?.toInt()
                                                UserDataStore.intelligence = document.getLong("intelligenceNumber")?.toInt()
                                            } else {
                                                Log.d("ProfileFragment", "No such document")
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.d("ProfileFragment", "Error getting document: ", it)
                                        }

                                }
                            }
                            .addOnFailureListener {
                                isLoginError = true
                                Log.d("ProfileFragment", "Error: ${it.message}")
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            } else {
                Button(
                    onClick = {
                        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            isSignUpError = true
                            Log.d("ProfileFragment", "Error: Empty fields")
                            return@Button
                        }
                        if (password != confirmPassword) {
                            isSignUpError = true
                            Log.d("ProfileFragment", "Error: Passwords do not match")
                            return@Button
                        }

                        val auth = Firebase.auth
                        val db = Firebase.firestore

                        db.collection("users")
                            .whereEqualTo("usernameString", username)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (documents.isEmpty) {
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                var user = auth.currentUser
                                                val userData = hashMapOf(
                                                    "accountLevelNumber" to 1,
                                                    "createdAtTimestamp" to Timestamp.now(),
                                                    "experiencePointNumber" to 0,
                                                    "healthNumber" to 100,
                                                    "intelligenceNumber" to 1,
                                                    "strengthNumber" to 1,
                                                    "usernameString" to username
                                                )
                                                db.collection("users").document(user!!.uid)
                                                    .set(userData)
                                                    .addOnSuccessListener {
                                                        Log.d("ProfileFragment", "User data saved")
                                                        UserDataStore.username = username
                                                        UserDataStore.accountLevel = 1
                                                        UserDataStore.experiencePoints = 0
                                                        UserDataStore.health = 100
                                                        UserDataStore.strength = 1
                                                        UserDataStore.intelligence = 1
                                                        isLoggedIn = true
                                                        saveLoginStatus(requireContext, true)
                                                    }
                                            } else {
                                                isSignUpError = true
                                                Log.d("ProfileFragment", "Error: ${task.exception?.message}")
                                            }
                                        }
                                } else {
                                    isSignUpError = true
                                    Log.d("ProfileFragment", "Error: Username already exists")
                                }
                            }

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isSignUpMode = !isSignUpMode
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSignUpMode) "Switch to Login" else "Switch to Sign Up")
            }
        }
    }

}

fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putBoolean("isLoggedIn", isLoggedIn)
        apply()
    }
}

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false) // Default is false
}
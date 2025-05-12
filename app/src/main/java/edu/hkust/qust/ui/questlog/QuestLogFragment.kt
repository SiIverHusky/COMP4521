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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
                    TaskAndQuestScreen(questLogViewModel)
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
fun TaskAndQuestScreen(questLogViewModel: QuestLogViewModel) {
    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = Firebase.auth

    var questNameList = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Tasks Section
        //Text(text = "Tasks", fontSize = 24.sp)
        /*
        Spacer(modifier = Modifier.height(8.dp))

        // Example of task items
        for (i in 1..3) {
            TaskItem(taskName = "Task $i")
        }
        */
        //Spacer(modifier = Modifier.height(16.dp))

        // Quests Section
        Text(text = "Quests", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        //QuestItem("hi", questNameList)
        //QuestItem("g", questNameList)
        val query = db.collection("questLog")
            .whereEqualTo("userIdString", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    //questNameList.add("${document.data}")
                    // Extract usernameString
                    val username = document["questDescriptionString"] as? String
                    Log.d("query name", username.toString())
                    // Save to the mutable list if not null
                    username?.let {
                        Log.d("add list", questNameList.toString())
                        if(!questNameList.contains(it.toString())) {
                            questNameList.add(it.toString())
                        }

                    }


                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        for (quest in questNameList) {
            QuestItem(quest, questNameList)
            Log.d(TAG,quest)
        }

        }

        Spacer(modifier = Modifier.height(16.dp))


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
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "✓", modifier = Modifier.clickable { /* Handle check */ })
        }
    }
}


@Composable
fun QuestItem(questName: String, questNameList: MutableList<String> = mutableListOf()) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = questName)

            Row(horizontalArrangement = Arrangement.End){
                Button (onClick = {questNameList.remove(questName)}, modifier = Modifier.width(85.dp)){
                    Text(text = "Done")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button (onClick = {questNameList.remove(questName)}, modifier = Modifier.width(88.dp)){
                    Text(text = "Delete")
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
package edu.hkust.qust.ui.questlog

import android.os.Bundle
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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.hkust.qust.databinding.FragmentQuestlogBinding

class QuestLogFragment : Fragment() {

    private var _binding: FragmentQuestlogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                TaskAndQuestScreen(questLogViewModel)
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


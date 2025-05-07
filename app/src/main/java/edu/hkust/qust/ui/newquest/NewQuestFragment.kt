package edu.hkust.qust.ui.newquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.hkust.qust.databinding.FragmentNewquestBinding

class NewQuestFragment : Fragment() {

    private var _binding: FragmentNewquestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
                NewTaskScreen(newQuestViewModel)
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
fun NewTaskScreen(newQuestViewModel: NewQuestViewModel) {
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
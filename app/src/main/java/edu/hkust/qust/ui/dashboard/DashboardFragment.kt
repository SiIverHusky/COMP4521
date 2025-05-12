package edu.hkust.qust.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.hkust.qust.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                UserProfileScreen(dashboardViewModel)
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
fun UserProfileScreen(dashboardViewModel: DashboardViewModel) {
    //Spacer(Modifier.padding(vertical = 10.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, top = 40.dp, end = 25.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Profile Image Placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text("Profile", fontSize = 16.sp)
        }

        // Name and Class
        Text("Name", Modifier.padding(top = 10.dp, bottom = 5.dp), fontSize = 24.sp)
        Text("Class", Modifier.padding(top = 15.dp, bottom = 5.dp), fontSize = 18.sp)

        // Level Progress Bar
        Text("Level 5", Modifier.padding(top = 15.dp, bottom = 3.dp))
        LinearProgressIndicator(
            progress = { 0.75f },
            modifier = Modifier.fillMaxWidth().padding(top = 3.dp, bottom = 10.dp)
        )

        // Other Progress Bars
        Text("20")
        LinearProgressIndicator(
            progress = { 0.2f },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 10.dp)
        )

        Text("15")
        LinearProgressIndicator(
            progress = { 0.15f },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 10.dp)
        )

        Text("100%")
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 10.dp)
        )

        // Current Task
        Text("Current Task", Modifier.padding(top = 35.dp, bottom = 5.dp), fontSize = 25.sp)
        Text("2/5", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming Tasks
        Text("Upcoming", fontSize = 25.sp)
        Text("2 days", Modifier.padding(start = 5.dp, top = 10.dp))
        //Spacer(modifier = Modifier.height(4.dp))
        Text("1 week", Modifier.padding(start = 5.dp, top = 10.dp))
        //Spacer(modifier = Modifier.height(8.dp))

        val selectedNavigationIndex = rememberSaveable {
            mutableIntStateOf(0)
        }

    }
}

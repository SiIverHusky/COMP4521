package edu.hkust.qust.ui.socialhub

import android.os.Bundle
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import edu.hkust.qust.R
import edu.hkust.qust.databinding.FragmentDashboardBinding
import edu.hkust.qust.databinding.FragmentNewquestBinding
import edu.hkust.qust.databinding.FragmentSocialhubBinding

class SocialHubFragment : Fragment() {

    private var _binding: FragmentSocialhubBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val socialHubViewModel =
            ViewModelProvider(this).get(SocialHubViewModel::class.java)

        _binding = FragmentSocialhubBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        return ComposeView(requireContext()).apply {
            setContent {
                Quest("COMP2711")
                Quest("COMP3711")
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
fun Quest(name: String) {
    Column(modifier= Modifier.padding(16.dp) ) {
        Text("Player request",    modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            textAlign = TextAlign.Center,
            fontSize = 48.sp
        )
        Box(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .height(100.dp)
                    .border(5.dp, Color.Black)
                    .background(Color.White),
            ){
                Row(){
                    Column(
                        modifier = Modifier.padding(end = 20.dp)
                    ){
                        Text("Need help!",modifier = Modifier.padding(start = 10.dp,top = 20.dp),
                            fontSize = 25.sp)
                        Row{
                            Text(name,modifier = Modifier.padding(start = 10.dp,top = 10.dp))
                            Text("HKO20",modifier = Modifier.padding(start = 10.dp,top = 10.dp))
                        }
                    }
                    Image(
                        modifier = Modifier
                            .padding(top = 20.dp,start = 50.dp)
                            .height(60.dp)
                            .width(60.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable(onClick = {  }),// add the sending onclick here
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.sent),
                        contentDescription = null
                    )

                }




            }
        }
        //Spacer(modifier = Modifier.weight(1f))
        //BottomBarWithImages()

    }
}
@Composable
fun MyIconImage(
    modifier: Modifier = Modifier,
    imageResId: Int,
    contentDescription: String? = null,
    size: Dp = 24.dp,
    onClick: () -> Unit,
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .clickable(onClick = onClick)

    )
}

@Composable
fun BottomBarWithImages() {
    Box(modifier = Modifier.fillMaxWidth()) {
        // 底部导航栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .border(5.dp, Color.Black)
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyIconImage(
                    imageResId = R.drawable.sent, //change image res
                    contentDescription = "Icon 1",
                    size = 40.dp,
                    onClick = {}//add the onclick function
                )
                MyIconImage(
                    imageResId = R.drawable.sent,//change image res
                    contentDescription = "Icon 2",
                    size = 40.dp,
                    onClick = {}//add the onclick function
                )

                Spacer(modifier = Modifier.size(40.dp))

                MyIconImage(
                    imageResId = R.drawable.sent,//change image res
                    contentDescription = "Icon 3",
                    size = 40.dp,
                    onClick = {} //add the onclick function
                )
                MyIconImage(
                    imageResId = R.drawable.sent,//change image res
                    contentDescription = "Icon 4",
                    size = 40.dp,
                    onClick = {}//add the onclick function
                )
            }
        }


        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .clip(CircleShape)
                .background(Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            MyIconImage(
                imageResId = R.drawable.sent,//change image res
                contentDescription = "Center Icon",
                size = 40.dp,
                onClick = {}//add the onclick function
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Quest("COMP2711")

}
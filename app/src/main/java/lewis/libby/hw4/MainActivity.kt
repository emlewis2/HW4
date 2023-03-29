package lewis.libby.hw4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import lewis.libby.hw4.ui.theme.HW4Theme
import lewis.libby.hw4.screens.Ui

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<GemViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HW4Theme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Ui(viewModel)
                }
            }
        }
    }
}
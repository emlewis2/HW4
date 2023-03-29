package lewis.libby.hw4.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import lewis.libby.hw4.R

@Composable
fun Pause(
    onClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title)) },
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding()
                    .fillMaxSize()
                    .clickable {
                        onClick()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.paused),
                        fontSize = 65.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}
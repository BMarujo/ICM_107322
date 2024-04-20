package com.example.movies_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movies_app.ui.theme.Movies_appTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Movies_appTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WellnessScreen()
                }
            }
        }
    }
}

@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier,
    wellnessViewModel: WellnessViewModel = viewModel()
) {
    var newItemTitle by rememberSaveable { mutableStateOf("") }
    var newItemDescription by rememberSaveable { mutableStateOf("") }
    val dialogVisibleState = rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {

        Button(
            onClick = { dialogVisibleState.value = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add Watch List Item")
        }

        WatchList(wellnessViewModel.watchList) { item ->
            wellnessViewModel.toggleWatched(item)
        }

        if (dialogVisibleState.value) {
            AddWatchListItemDialog(
                onAddItem = {
                    wellnessViewModel.addWatchListItem(
                        WatchListItem(
                            id = wellnessViewModel.watchList.size,
                            title = newItemTitle,
                            description = newItemDescription
                        )
                    )
                    dialogVisibleState.value = false
                    // Clear fields after adding
                    newItemTitle = ""
                    newItemDescription = ""
                },
                onDismiss = { dialogVisibleState.value = false },
                title = newItemTitle,
                description = newItemDescription,
                onTitleChange = { newItemTitle = it },
                onDescriptionChange = { newItemDescription = it }
            )
        }
    }
}


@Composable
fun WatchList(
    watchList: List<WatchListItem>,
    onWatchedToggle: (WatchListItem) -> Unit
) {
    LazyColumn {
        items(
            items = watchList,
            key = { item -> item.id }
        ) { item ->
            WatchListItem(
                item = item,
                onWatchedToggle = onWatchedToggle
            )
        }
    }
}

@Composable
fun WatchListItem(
    item: WatchListItem,
    onWatchedToggle: (WatchListItem) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Checkbox(
            checked = item.watched,
            onCheckedChange = { onWatchedToggle(item) }
        )
        Text(
            text = item.title,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

class WellnessViewModel : ViewModel() {
    private val _watchList = mutableListOf<WatchListItem>()
    val watchList: List<WatchListItem>
        get() = _watchList

    init {
        // Pre-fill with some sample entries
        repeat(5) { index ->
            _watchList.add(
                WatchListItem(
                    id = index,
                    title = "Movie/series ${index + 1}",
                    description = "Description for movie/series ${index + 1}"
                )
            )
        }
    }

    fun addWatchListItem(item: WatchListItem) {
        _watchList.add(item)
    }

    fun removeWatchListItem(item: WatchListItem) {
        _watchList.remove(item)
    }

    fun toggleWatched(item: WatchListItem) {
        item.watched = !item.watched
    }
}

@Composable
fun AddWatchListItemDialog(
    onAddItem: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Watch List Item") },
        confirmButton = {
            Button(onClick = onAddItem) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.padding(8.dp)
                )
                TextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    )
}


data class WatchListItem(
    val id: Int,
    val title: String,
    val description: String,
    var watched: Boolean = false
)

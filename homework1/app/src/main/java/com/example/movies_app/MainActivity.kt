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
    var checkedItems by rememberSaveable { mutableStateOf(wellnessViewModel.getCheckedItems()) }

    var newItemTitle by rememberSaveable { mutableStateOf("") }
    val dialogVisibleState = rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {

        Button(
            onClick = { dialogVisibleState.value = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add Watch List Item")
        }

        WatchList(
            watchList = wellnessViewModel.watchList,
            checkedItems = checkedItems,
            onWatchedToggle = { index, isChecked ->
                checkedItems = checkedItems.toMutableList().also { it[index] = isChecked }
                wellnessViewModel.toggleWatched(index)
            }
        )

        if (dialogVisibleState.value) {
            AddWatchListItemDialog(
                onAddItem = {
                    wellnessViewModel.addWatchListItem(
                        WatchListItem(
                            id = wellnessViewModel.watchList.size,
                            title = newItemTitle
                        )
                    )
                    checkedItems = checkedItems.toMutableList().apply { add(false) } // Add a new unchecked item
                    dialogVisibleState.value = false
                    newItemTitle = ""
                },
                onDismiss = { dialogVisibleState.value = false },
                title = newItemTitle,
                onTitleChange = { newItemTitle = it }
            )
        }
    }
}

class WellnessViewModel : ViewModel() {
    private val _watchList = mutableListOf<WatchListItem>()
    private val _checkedItems = mutableListOf<Boolean>()

    val watchList: List<WatchListItem>
        get() = _watchList

    init {
        repeat(5) { index ->
            _watchList.add(
                WatchListItem(
                    id = index,
                    title = "Movie/series ${index + 1}"
                )
            )
            _checkedItems.add(false)
        }
    }

    fun addWatchListItem(item: WatchListItem) {
        _watchList.add(item)
        _checkedItems.add(false)
    }

    fun toggleWatched(index: Int) {
        _watchList[index].watched = !_watchList[index].watched
    }

    fun getCheckedItems(): List<Boolean> {
        return _checkedItems
    }
}

@Composable
fun WatchList(
    watchList: List<WatchListItem>,
    checkedItems: List<Boolean>,
    onWatchedToggle: (Int, Boolean) -> Unit
) {
    LazyColumn {
        items(
            items = watchList,
            key = { item -> item.id }
        ) { item ->
            val index = watchList.indexOf(item)
            WatchListItem(
                item = item,
                isChecked = checkedItems[index],
                onWatchedToggle = { isChecked ->
                    onWatchedToggle(index, isChecked)
                }
            )
        }
    }
}

@Composable
fun WatchListItem(
    item: WatchListItem,
    isChecked: Boolean,
    onWatchedToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onWatchedToggle(it) }
        )
        Text(
            text = item.title,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

@Composable
fun AddWatchListItemDialog(
    onAddItem: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
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
            }
        }
    )
}

data class WatchListItem(
    val id: Int,
    val title: String,
    var watched: Boolean = false
)
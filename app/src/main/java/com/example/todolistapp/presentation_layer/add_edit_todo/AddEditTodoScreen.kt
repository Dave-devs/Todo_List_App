package com.example.todolistapp.presentation_layer.add_edit_todo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolistapp.util.UiEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddEditTodoScreen(
    viewModel: AddEditTodoViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect{ event->
            when(event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                // Provide Title
                title = {
                    Text(
                        text = "Add Edit Todo",
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colors.secondary
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddEditTodoEvent.OnSaveTodoClick) }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Check"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TextField(
                value = viewModel.title,
                onValueChange = {
                    viewModel.onEvent(AddEditTodoEvent.OnTitleChange(it))
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Title")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.content,
                onValueChange = {
                    viewModel.onEvent(AddEditTodoEvent.OnContentChange(it))
                },
                placeholder = {
                    Text(text = "Content")
                },
                singleLine = false,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
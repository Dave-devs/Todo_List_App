package com.example.todolistapp.presentation_layer.add_edit_todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.data.Todo
import com.example.todolistapp.data.TodoRepository
import com.example.todolistapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    /*  Returns new value passed for our todo variables
        in our todo entity table by passing <Todo?>. If we want
        to add a new value to our todo here it will simple stay null
    */
    var todo by mutableStateOf<Todo?>(null)
        private set

    /* Return a new MutableState initialized with the passed in value for the title. */
    var title by mutableStateOf("")
        private set

    /* Return a new MutableState initialized with the passed in value for the content. */
    var content by mutableStateOf("")
        private set

    //Function to get the one-time event that we can perform in our app.
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    //Function to send the UiEvent of the app in a coroutine scope.
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch{
            _uiEvent.send(event)
        }
    }

    /*  It will be executed as soon as our view model is initialize.
    *   Did we actually open the AddEdit page by clicking on
    *   existing (NTodo or by clicking on Adding a new NTodo?
    *   If we open it from existing NTodo, then we want to
    *   load this from the Database by its ID.*/
    init {
        val todoId = savedStateHandle.get<Int?>("todoId")!!
        //We can't have nullable Int so we use -1
        if(todoId != -1) {
            viewModelScope.launch {
                repository.getTodoById(todoId)?.let { todo ->
                 title = todo.title
                 content = todo.content ?: ""
                    this@AddEditTodoViewModel.todo = todo
                }
            }
        }
    }

    //What could the user possibly do on the app, they could;
    fun onEvent(event: AddEditTodoEvent) {
        //Check if the event is on a specific function and do the task;
        when(event) {
            //Function to change title on our todo.
            is AddEditTodoEvent.OnTitleChange -> {
                title = event.title
            }
            //Function to change content on our todo.
            is AddEditTodoEvent.OnContentChange -> {
                content = event.content
            }
            //Function to save the changes made on our todo.
            is AddEditTodoEvent.OnSaveTodoClick -> {
                viewModelScope.launch {
                    if(title.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackBar(
                            message = "Title can't be blank!",
                            action = null
                        ))
                        return@launch
                    }
                    repository.insertTodo(
                        Todo(
                            title = title,
                            content = content,
                            isDone = todo?.isDone ?: false,
                            id = todo?.id
                        )
                    )
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
        }
    }
}
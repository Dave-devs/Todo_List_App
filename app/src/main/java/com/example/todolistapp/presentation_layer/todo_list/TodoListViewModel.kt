package com.example.todolistapp.presentation_layer.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.data.Todo
import com.example.todolistapp.data.TodoRepository
import com.example.todolistapp.util.Routes
import com.example.todolistapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    //Command to request all our Todos from the database.
    val todos = repository.getTodos()

    //Function to get the one-time event that we can perform in our app.
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedTodo: Todo? = null

    //Function to send the UiEvent of the app in a coroutine scope.
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch{
            _uiEvent.send(event)
        }
    }

    //Asynchronous coroutine function to create the task the user could perform on our app in Flow.
    fun onEvent(event: TodoListEvent) {
        //Check if the event is on a specific function and do the task;
        when(event) {
            //Click on existing Todo, do this;
            is TodoListEvent.OnTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=$event.todo.id"))
            }
            //Click on add new button, do this;
            is TodoListEvent.OnAddTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO))
            }
            //Click on delete existing Todo(s) with the icon, do this;
            is TodoListEvent.OnDeleteTodoClick -> {
                viewModelScope.launch{
                    deletedTodo = event.todo
                    repository.deleteTodo(event.todo)
                    sendUiEvent(UiEvent.ShowSnackBar(
                        message = "Todo deleted!",
                        action = "Undo"
                    ))
                }
            }
            //Click on the 'Undo' from the Snackbar(to undo delete of Todo), do this;
            is TodoListEvent.OnUndoDeleteClick -> {
                deletedTodo?.let { todo ->
                    viewModelScope.launch {
                        repository.insertTodo(todo)
                        sendUiEvent(UiEvent.ShowSnackBar(
                            message = "Todo undeleted!",
                            action = null
                        ))
                    }
                }
            }
            //To save the change made on existing Todo, do this;
            is TodoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    repository.insertTodo(
                        event.todo.copy(
                           isDone = event.isDone
                        )
                    )
                }
            }
        }
    }
}
package com.example.todolistapp.presentation_layer.add_edit_todo

sealed class AddEditTodoEvent {
    data class OnTitleChange(val title: String): AddEditTodoEvent()
    data class OnContentChange(val content: String): AddEditTodoEvent()
    object OnSaveTodoClick: AddEditTodoEvent()
}

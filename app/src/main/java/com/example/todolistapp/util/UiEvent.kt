package com.example.todolistapp.util

sealed class UiEvent{
    data class Navigate(val route: String): UiEvent()
    data class ShowSnackbar(val message: String, val action: String?): UiEvent()
    object PopBackStack: UiEvent()
}

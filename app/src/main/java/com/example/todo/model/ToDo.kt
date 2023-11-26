package com.example.todo.model

data class ToDo(

    val text: String="",
    var isDone:Boolean=false,
    val userId: String="",
    val timestamp: Long= 0L
)

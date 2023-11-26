package com.example.todo.data

import com.example.todo.model.ToDo

object DummyData {

    fun getData():ArrayList<ToDo>{
        return arrayListOf(
            ToDo(text = "First Todo"),
            ToDo(text = "Second Todo"),
            ToDo(text = "Third Todo"),
            ToDo(text = "Fourth Todo"),
            ToDo(text = "First Todo"),
            ToDo(text = "First Todo"),
            ToDo(text = "First Todo"),
            ToDo(text = "First Todo"),
            ToDo(text = "First Todo"),
        )
    }
}
package com.example.todo.presentation.todo_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.ToDo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val userUid = firebaseAuth.currentUser?.uid ?: "0"


    val insertLiveData = MutableLiveData<Boolean>()
    val toDos = MutableLiveData<List<ToDo>>()

    fun addTodo(text: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val todoReference = firebaseDatabase.getReference("ToDos")
                val timestamp = System.currentTimeMillis()
                todoReference.child(userUid).child(timestamp.toString()).setValue(
                    ToDo(text = text, userId = userUid, timestamp = timestamp)
                )
                withContext(Dispatchers.Main) {
                    insertLiveData.value = true
                    fetchToDos()
                }
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    fun fetchToDos() {
        viewModelScope.launch {
            try{
                val todoReference = firebaseDatabase.getReference("ToDos")
                val query = todoReference.child(userUid).orderByChild("timestamp")
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val todosList = mutableListOf<ToDo>()
                        for (dataSnapshot in snapshot.children) {
                            val todo = dataSnapshot.getValue(ToDo::class.java)
                            todo?.let { todosList.add(it) }
                        }
                        toDos.value = todosList
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }catch (e:Exception){

            }
        }
    }

    fun changeTodoState(timeStamp:String,isDone:Boolean){
        viewModelScope.launch {
            val todo= firebaseDatabase.getReference("ToDos").child(userUid).child(timeStamp)
            try {
                withContext(Dispatchers.IO) {
                    todo.child("done").setValue(isDone)
                }
            } catch (e: Exception) {
                // Handle error
            }

        }
    }

    fun deleteCompleted() {
        viewModelScope.launch {
            try {
                val todoReference = firebaseDatabase.getReference("ToDos").child(userUid)
                val query = todoReference.orderByChild("done").equalTo(true)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { dataSnapshot ->
                            dataSnapshot.ref.removeValue()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    fun editTodo(timeStamp: String, newText: String) {
        viewModelScope.launch {
            val todoReference = firebaseDatabase.getReference("ToDos").child(userUid).child(timeStamp)
            try {
                withContext(Dispatchers.IO) {
                    val updatedData = hashMapOf<String, Any>("text" to newText)
                    todoReference.updateChildren(updatedData)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteTodo(todo: ToDo) {
        viewModelScope.launch {
            try {
                val todoReference = firebaseDatabase.getReference("ToDos")
                todoReference.child(userUid).child(todo.timestamp.toString()).removeValue()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


}

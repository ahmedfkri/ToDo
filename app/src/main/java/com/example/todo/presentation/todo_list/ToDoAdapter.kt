package com.example.todo.presentation.todo_list

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.TodoListItemBinding
import com.example.todo.model.ToDo
import com.example.todo.util.DateAndTime

class ToDoAdapter : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

     var list = arrayListOf<ToDo>()
     var listener:OnItemClickListener?=null


    inner class ToDoViewHolder(val binding: TodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {

        return ToDoViewHolder(
            TodoListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {

        val currentToDo = list[position]
        holder.binding.checkBox.text = currentToDo.text
        if(DateAndTime.isTimestampToday(currentToDo.timestamp)){
            holder.binding.txtDate.text="Today"
        }else{
            holder.binding.txtDate.text="Earlier"
        }

        holder.binding.checkBox.isChecked=currentToDo.isDone

        if (currentToDo.isDone) {
            val paint = holder.binding.checkBox.paint
            paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            val paint = holder.binding.checkBox.paint
            paint.flags = paint.flags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }


        holder.binding.checkBox.setOnClickListener {
            currentToDo.isDone=!currentToDo.isDone
            listener?.onCheckBoxClick(currentToDo)
        }
        holder.binding.root.setOnClickListener {
            listener?.onItemClick(currentToDo)
        }


    }

    fun setData(toDos: List<ToDo>) {
        list.clear()
        list.addAll(toDos)
        notifyDataSetChanged()
    }
}

interface OnItemClickListener {
    fun onItemClick(todo: ToDo)
    fun onCheckBoxClick(todo: ToDo)
}
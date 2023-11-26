package com.example.todo.presentation.todo_list

import AuthViewModel
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.data.MySharedPref
import com.example.todo.databinding.FragmentToDoListBinding
import com.example.todo.model.ToDo
import com.example.todo.presentation.MainActivity
import com.example.todo.util.DateAndTime
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class ToDoListFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener,
    OnItemClickListener {

    private lateinit var binding: FragmentToDoListBinding
    private lateinit var itemTouchHelperCallback: ItemTouchHelper.Callback
    private lateinit var toggle: ActionBarDrawerToggle
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var adapter: ToDoAdapter
    private var toDos = arrayListOf<ToDo>()
    private val sharedViewModel: SharedViewModel by viewModels()


    private lateinit var navHeaderImage: ShapeableImageView
    private lateinit var navHeaderMail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoListBinding.inflate(inflater, container, false)

        setupDrawer()
        binding.navView.setNavigationItemSelectedListener(this)
        accessHeaderData()
        setupHeaderData()
        setUpSwipeActions()
        binding.txtDate.text=DateAndTime.getCurrentDateTime(System.currentTimeMillis())

        binding.fabAdd.setOnClickListener {
            showAddBottomDialog()
        }
        setupRecyclerView()
        adapter.listener = this

        return binding.root
    }

    private fun setUpSwipeActions() {
         itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val todo = adapter.list[position]

                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                   Snackbar.make(requireView(),"Delete Task?",Snackbar.LENGTH_LONG).setAction("Undo"){
                       observeToDos()
                   }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                       override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                           if (event != DISMISS_EVENT_ACTION) {

                               sharedViewModel.deleteTodo(todo)
                               observeToDos()
                           }
                       }
                   }).show()
            }
        }

         }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
    }

    private fun showEditBottomDialog(todo:ToDo){
        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.edit_bottom_sheet)
            show()
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.BOTTOM)
        }

        val todoEditText = dialog.findViewById<EditText>(R.id.edtTodoEdit)
        todoEditText.setText(todo.text)

        dialog.findViewById<Button>(R.id.btnEditNote).setOnClickListener {

            val todoText = todoEditText.text.toString()
            if (todoText.isNotEmpty()) {
                sharedViewModel.editTodo(todo.timestamp.toString(),todoText)
                //Toast.makeText(requireContext(), "ToDo Edited", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            } else {
                Toast.makeText(requireContext(), "please enter valid value", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showAddBottomDialog() {
        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.add_bottom_sheet)
            show()
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.BOTTOM)
        }
        dialog.findViewById<Button>(R.id.btnAddNote).setOnClickListener {

            val todoEditText = dialog.findViewById<EditText>(R.id.edtTodo)
            val todoText = todoEditText.text.toString()
            if (todoText.isNotEmpty()) {
                sharedViewModel.addTodo(todoText)
                //Toast.makeText(requireContext(), "ToDo added", Toast.LENGTH_SHORT).show()
                todoEditText.text.clear()

            } else {
                Toast.makeText(requireContext(), "please enter valid value", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun observeToDos() {
        try{
            sharedViewModel.fetchToDos()
            sharedViewModel.toDos.observe(viewLifecycleOwner) { todos ->
                adapter.setData(todos)
                binding.progBar.visibility=View.GONE
                if (todos.isNotEmpty()) {

                    binding.imageView.visibility = View.GONE
                } else {
                    binding.imageView.visibility = View.VISIBLE
                }
            }
        }catch(e: IOException){
            binding.progBar.visibility=View.GONE
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupRecyclerView() {
        adapter = ToDoAdapter()
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.rv.adapter = adapter
        observeToDos()
    }

    override fun onItemClick(todo: ToDo) {
       // Toast.makeText(requireContext(), todo.isDone.toString(), Toast.LENGTH_SHORT).show()
        showEditBottomDialog(todo)
    }

    override fun onCheckBoxClick(todo: ToDo) {
        sharedViewModel.changeTodoState(todo.timestamp.toString(), todo.isDone)
    }


    private fun setupHeaderData() {
        navHeaderMail.text = authViewModel.currentUser.value?.email
    }

    private fun accessHeaderData() {
        val header = binding.navView.getHeaderView(0)
        navHeaderMail = header.findViewById(R.id.txtMail)
        navHeaderImage = header.findViewById(R.id.nav_avatar)
    }

    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.imgMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.itemSignOut -> {
            authViewModel.signOut()
            MySharedPref.putBool("isSigned", false)
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            true
        }

        R.id.itemDeleteCompleted -> {
           sharedViewModel.deleteCompleted()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }


}
package com.example.todo.presentation

import AuthViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.todo.R
import com.example.todo.data.MySharedPref
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.presentation.auth.LogInFragment
import com.example.todo.presentation.auth.SignUpFragmentDirections
import com.example.todo.presentation.get_started.GetStartedFragment
import com.example.todo.presentation.get_started.GetStartedFragmentDirections
import com.example.todo.presentation.todo_list.ToDoListFragment
import com.example.todo.presentation.todo_list.ToDoListFragmentDirections
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        if (MySharedPref.getBool("isSigned", false)) {
            Handler(Looper.getMainLooper()).postDelayed({
                showList()
            }, 3000)

        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                getStarted()
            }, 3000)
        }


    }

    private fun getStarted() {
        navController.navigate(SplashFragmentDirections.actionSplashFragmentToGetStartedFragment())
    }

    private fun showList() {
        navController.navigate(SplashFragmentDirections.actionSplashFragmentToToDoListFragment())
    }


}
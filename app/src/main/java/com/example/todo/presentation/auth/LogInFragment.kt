package com.example.todo.presentation.auth

import AuthViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todo.data.MySharedPref
import com.example.todo.databinding.FragmentLogInBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.btnLogIn.setOnClickListener {
            signIn()
        }

        return binding.root
    }

    private fun signIn() {
        val email = binding.edtEmail.text.toString().trim()
        val pass = binding.edtPass.text.toString().trim()

        lifecycleScope.launch {
            try {
                authViewModel.signIn(email, pass).await()
                MySharedPref.putBool("isSigned",true)
                findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToToDoListFragment())
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

            }
        }

    }

}
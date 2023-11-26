package com.example.todo.presentation.auth

import AuthViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.data.MySharedPref
import com.example.todo.databinding.FragmentSignUpBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.txtLogin.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPass.text.toString().trim()
            val pass2 = binding.edtPassAgain.text.toString().trim()

            when (validateData(email, pass, pass2)) {

                PASS_DOESNT_MATCH_ERROR ->  Toast.makeText(
                    requireContext(),
                    getString(R.string.pass_dont_match),
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                   signUp(email,pass)
                }
            }

        }

        return binding.root
    }

    private fun validateData(email: String, pass: String, passAgain: String): Int {
        binding.apply {
            return when {
                email.isEmpty() -> NO_EMAIL_ERROR
                pass.isEmpty() -> NO_PASS_ERROR
                passAgain.isEmpty() -> NO_PASS_ERROR
                pass != passAgain -> PASS_DOESNT_MATCH_ERROR
                else -> VALID
            }
        }

    }

    private fun signUp(email:String,pass:String) {
        lifecycleScope.launch {
            try {
                authViewModel.signUp(email, pass).await()
                MySharedPref.putBool("isSigned", true)
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToToDoListFragment())
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_LONG).show()
            }
        }


    }



    companion object {
        const val VALID = 0
        const val NO_EMAIL_ERROR = 1
        const val NO_PASS_ERROR = 2
        const val PASS_DOESNT_MATCH_ERROR = 3
    }


}
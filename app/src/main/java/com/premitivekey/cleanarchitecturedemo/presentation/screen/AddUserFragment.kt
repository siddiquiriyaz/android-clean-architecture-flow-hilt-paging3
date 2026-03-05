package com.premitivekey.cleanarchitecturedemo.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.premitivekey.cleanarchitecturedemo.R
import com.premitivekey.cleanarchitecturedemo.databinding.FragmentAddUserBinding
import com.premitivekey.cleanarchitecturedemo.presentation.base.UiState
import com.premitivekey.cleanarchitecturedemo.presentation.viewmodel.AddUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddUserFragment : Fragment(R.layout.fragment_add_user) {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddUserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddUserBinding.bind(view)

        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                viewModel.createUser(
                    firstName = binding.etFirstName.text.toString().trim(),
                    lastName  = binding.etLastName.text.toString().trim(),
                    email     = binding.etEmail.text.toString().trim(),
                )
            }
        }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createUserState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            binding.progressBar.isVisible = false
                            binding.tvError.isVisible     = false
                            binding.btnSave.isEnabled     = true
                        }
                        is UiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.tvError.isVisible     = false
                            binding.btnSave.isEnabled     = false
                        }
                        is UiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.btnSave.isEnabled     = true
                            Toast.makeText(
                                requireContext(),
                                "${state.data.firstName} ${state.data.lastName} added!",
                                Toast.LENGTH_SHORT
                            ).show()
                            clearForm()
                            viewModel.resetState()
                        }
                        is UiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.tvError.isVisible     = true
                            binding.tvError.text          = state.message
                            binding.btnSave.isEnabled     = true
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName  = binding.etLastName.text.toString().trim()
        val email     = binding.etEmail.text.toString().trim()

        binding.layoutFirstName.error = null
        binding.layoutLastName.error  = null
        binding.layoutEmail.error     = null

        if (firstName.isEmpty()) {
            binding.layoutFirstName.error = "First name is required"
            return false
        }
        if (lastName.isEmpty()) {
            binding.layoutLastName.error = "Last name is required"
            return false
        }
        if (email.isEmpty()) {
            binding.layoutEmail.error = "Email is required"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.error = "Invalid email format"
            return false
        }
        return true
    }

    private fun clearForm() {
        binding.etFirstName.text?.clear()
        binding.etLastName.text?.clear()
        binding.etEmail.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
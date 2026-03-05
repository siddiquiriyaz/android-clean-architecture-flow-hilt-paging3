package com.premitivekey.cleanarchitecturedemo.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.premitivekey.cleanarchitecturedemo.R
import com.premitivekey.cleanarchitecturedemo.databinding.FragmentUserListBinding
import com.premitivekey.cleanarchitecturedemo.presentation.adapter.UserAdapter
import com.premitivekey.cleanarchitecturedemo.presentation.base.UiState
import com.premitivekey.cleanarchitecturedemo.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : Fragment(R.layout.fragment_user_list) {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private val adapter = UserAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserListBinding.bind(view)
        setupRecyclerView()
        observeState()
        observeLoadingMore()   // ← separate observer for bottom loader
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvUser.layoutManager = layoutManager
        binding.rvUser.adapter = adapter

        binding.rvUser.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val visibleCount     = layoutManager.childCount
                val totalCount       = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val nearBottom       = (visibleCount + firstVisibleItem) >= totalCount - 3
                if (nearBottom && !viewModel.isLastPage) {
                    viewModel.loadUsers()
                }
            }
        })
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usersState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            binding.progressBar.isVisible = false
                            binding.tvError.isVisible     = false
                        }
                        is UiState.Loading -> {
                            binding.progressBar.isVisible = true   // centre spinner first load only
                            binding.tvError.isVisible     = false
                        }
                        is UiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.tvError.isVisible     = false
                            adapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.tvError.isVisible     = true
                            binding.tvError.text          = state.message
                        }
                    }
                }
            }
        }
    }

    // controls bottom paging spinner independently
    private fun observeLoadingMore() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingMore.collect { isLoading ->
                    binding.progressPaging.isVisible = isLoading
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
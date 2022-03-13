package dev.keader.correiostracker.view.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.FragmentAuthorsBinding
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.adapters.AuthorsAdapter

@AndroidEntryPoint
class AuthorsFragment : Fragment() {
    private lateinit var binding: FragmentAuthorsBinding
    private val authorsViewModel: AuthorsViewModel by viewModels()
    private val authorsAdapter = AuthorsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAuthorsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewAuthors.adapter = authorsAdapter
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerViewAuthors.layoutManager = gridLayoutManager
        setupObservers()
    }

    private fun setupObservers() {
        authorsViewModel.authors.distinctUntilChanged().observe(viewLifecycleOwner) {
            authorsAdapter.submitList(it)
            binding.recyclerViewAuthors.isVisible = it.isNotEmpty()
            binding.progressAutors.isVisible = it.isEmpty()
        }
    }
}

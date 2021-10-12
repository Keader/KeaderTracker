package dev.keader.correiostracker.view.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAuthorsBinding.inflate(inflater, container, false)

        val authorsAdapter = AuthorsAdapter()
        val concatAdapter = ConcatAdapter(authorsAdapter)
        binding.recyclerViewAuthors.adapter = concatAdapter

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerViewAuthors.layoutManager = gridLayoutManager

        authorsViewModel.authors.distinctUntilChanged().observe(viewLifecycleOwner, {
            handleWithListVisibility(it.isEmpty())
            if (it.isNotEmpty())
                authorsAdapter.submitList(it)
        })

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun handleWithListVisibility(empty: Boolean) {
        if (empty) {
            binding.recyclerViewAuthors.visibility = View.GONE
            binding.progressAutors.visibility = View.VISIBLE
        }
        else {
            binding.recyclerViewAuthors.visibility = View.VISIBLE
            binding.progressAutors.visibility = View.GONE
        }
    }
}

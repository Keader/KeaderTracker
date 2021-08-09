package dev.keader.correiostracker.view.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.FragmentAuthorsBinding
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.adapters.AuthorsAdapter
import dev.keader.correiostracker.view.adapters.AuthorsHeaderAdapter

@AndroidEntryPoint
class AuthorsFragment : Fragment() {

    private lateinit var binding: FragmentAuthorsBinding
    private val authorsViewModel: AuthorsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAuthorsBinding.inflate(inflater, container, false)

        val authorsAdapter = AuthorsAdapter()
        val concatAdapter = ConcatAdapter(AuthorsHeaderAdapter(), authorsAdapter)

        binding.recyclerViewAuthors.adapter = concatAdapter

        authorsViewModel.authors.distinctUntilChanged().observe(viewLifecycleOwner, {
            if (it.isNotEmpty())
                authorsAdapter.submitList(it)
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}

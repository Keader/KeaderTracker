package dev.keader.correiostracker.view.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.FragmentAuthorsBinding
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.adapters.AuthorsAdapter

@AndroidEntryPoint
class AuthorsFragment : Fragment() {
    private lateinit var binding: FragmentAuthorsBinding
    private val authorsViewModel: AuthorsViewModel by viewModels()
    private val itemTouchHelper by lazy {
        val dragDirections = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val callback = object: ItemTouchHelper.SimpleCallback(dragDirections, 0) {
            override fun isLongPressDragEnabled() = false

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                onMoveCalled(viewHolder, target)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
        }

        ItemTouchHelper(callback)
    }
    private val authorsAdapter = AuthorsAdapter(itemTouchHelper)

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
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewAuthors)
        setupObservers()
    }

    fun onMoveCalled(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        authorsAdapter.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
    }

    private fun setupObservers() {
        authorsViewModel.authors.distinctUntilChanged().observe(viewLifecycleOwner) {
            authorsAdapter.submitList(it)
            binding.recyclerViewAuthors.isVisible = it.isNotEmpty()
            binding.progressAutors.isVisible = it.isEmpty()
        }
    }
}

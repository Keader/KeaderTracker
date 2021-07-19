package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.databinding.ListItemAuthorsHeaderBinding

class AuthorsHeaderAdapter : RecyclerView.Adapter<AuthorsHeaderAdapter.AuthorHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorHeaderViewHolder {
        return AuthorHeaderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AuthorHeaderViewHolder, position: Int) {}

    class AuthorHeaderViewHolder private constructor(val binding: ListItemAuthorsHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): AuthorHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAuthorsHeaderBinding.inflate(layoutInflater, parent, false)
                return AuthorHeaderViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = 1
}

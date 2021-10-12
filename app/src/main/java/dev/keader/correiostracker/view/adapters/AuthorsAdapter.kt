package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.ListItemAuthorsBinding
import dev.keader.correiostracker.network.GithubAuthor


class AuthorsAdapter : ListAdapter<GithubAuthor, AuthorsAdapter.AuthorViewHolder>(AuthorsAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        return AuthorViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        val author = getItem(position)
        holder.bind(author)
    }

    class AuthorViewHolder private constructor(val binding: ListItemAuthorsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(author: GithubAuthor) {
            binding.author = author
            if (author.link.contains("linkedin"))
                binding.iconSite.setImageResource(R.drawable.ic_linkedin)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AuthorViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAuthorsBinding.inflate(layoutInflater, parent, false)
                return AuthorViewHolder(binding)
            }
        }
    }

    private companion object : DiffUtil.ItemCallback<GithubAuthor>() {
        override fun areItemsTheSame(oldItem: GithubAuthor, newItem: GithubAuthor): Boolean {
            return oldItem.login == newItem.login
        }
        override fun areContentsTheSame(oldItem: GithubAuthor, newItem: GithubAuthor): Boolean {
            return oldItem == newItem
        }
    }
}

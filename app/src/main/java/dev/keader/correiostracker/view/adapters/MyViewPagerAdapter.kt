package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.ListItemAuthorsBinding
import dev.keader.correiostracker.network.GithubAuthor

class MyViewPagerAdapter : RecyclerView.Adapter<MyViewPagerAdapter.SliderViewHolder>() {
    val sliders = mutableListOf<GithubAuthor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val author = sliders[position]
        holder.bind(author)
    }

    override fun getItemCount() = sliders.size

    class SliderViewHolder private constructor(val binding: ListItemAuthorsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(author: GithubAuthor) {
            binding.author = author
            if (author.link.contains("linkedin"))
                binding.iconSite.setImageResource(R.drawable.ic_linkedin)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SliderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAuthorsBinding.inflate(layoutInflater, parent, false)
                return SliderViewHolder(binding)
            }
        }
    }
}

package dev.keader.correiostracker.archived

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentArchivedBinding
import dev.keader.correiostracker.databinding.FragmentHomeBinding


class ArchivedFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentArchivedBinding>(inflater, R.layout.fragment_archived, container, false)
        return binding.root
    }
}
package dev.keader.correiostracker.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.FragmentHomeBinding
import dev.keader.correiostracker.model.EventObserver
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.network.GithubAuthor
import dev.keader.correiostracker.view.adapters.ListItemListener
import dev.keader.correiostracker.view.adapters.MyViewPagerAdapter
import dev.keader.correiostracker.view.adapters.TrackAdapter
import dev.keader.correiostracker.view.adapters.TrackHeaderAdapter
import dev.keader.correiostracker.view.dontkill.DontKillFragment
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val navController
        get() = findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerAdapter = TrackHeaderAdapter()
        val trackAdapter = TrackAdapter(ListItemListener { code ->
            homeViewModel.onItemTrackClicked(code)
        })
        val concatAdapter = ConcatAdapter(headerAdapter, trackAdapter)

        binding.recyclerViewDelivery.adapter = concatAdapter

        homeViewModel.tracks.distinctUntilChanged().observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                showEmptyList()
            } else {
                trackAdapter.submitList(it)
                showRecyclerView()
            }
        })

        homeViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, EventObserver { code ->
            navController.navigate(HomeFragmentDirections.actionGlobalTrackDetailFragment(code))
        })

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.onRefreshCalled()
        }

        homeViewModel.eventRefreshRunning.observe(viewLifecycleOwner, { running ->
            binding.swipeRefresh.isRefreshing = running
        })

        if (homeViewModel.shouldShowDontKillAlert()) {
            homeViewModel.saveDontKillAlert()
            DontKillFragment().show(parentFragmentManager, "DontKill")
        }

        val viewPagerAdapter = MyViewPagerAdapter()
        val list = mutableListOf<GithubAuthor>()
        val avatar = "https://i.imgur.com/DGWRgy0.jpeg"
        list.add(GithubAuthor("Mima", avatar, avatar, 10))
        list.add(GithubAuthor("Mima", avatar, avatar, 9))
        list.add(GithubAuthor("Mima", avatar, avatar, 8))
        list.add(GithubAuthor("Mima", avatar, avatar, 7))
        list.add(GithubAuthor("Mima", avatar, avatar, 6))
        list.add(GithubAuthor("Mima", avatar, avatar, 5))
        list.add(GithubAuthor("Mima", avatar, avatar, 4))
        list.add(GithubAuthor("Mima", avatar, avatar, 3))
        list.add(GithubAuthor("Mima", avatar, avatar, 2))
        list.add(GithubAuthor("Mima", avatar, avatar, 1))
        viewPagerAdapter.sliders.addAll(list)
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = 3
        val composeTransformer = CompositePageTransformer()
        composeTransformer.addTransformer(MarginPageTransformer(40))
        composeTransformer.addTransformer { page, position ->
//            val r = 1 - abs(position)
//            page.scaleY = (0.80f + r * 0.10f)
            page.apply {
                val pageWidth = width
                val pageHeight = height
                val MIN_SCALE = 0.85f
                val MIN_ALPHA = 0.5f
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Timber.d(list[position].contributions.toString())
            }
        })

        binding.viewPager.setPageTransformer(composeTransformer)

    }

    private fun showEmptyList() {
        binding.recyclerViewDelivery.visibility = View.GONE
        binding.recylerViewEmpty.root.visibility = View.VISIBLE
    }

    private fun showRecyclerView() {
        binding.recyclerViewDelivery.visibility = View.VISIBLE
        binding.recylerViewEmpty.root.visibility = View.GONE
    }
}

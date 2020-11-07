package dev.keader.correiostracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dev.keader.correiostracker.databinding.ActivityMainBinding
import dev.keader.correiostracker.archived.ArchivedFragment
import dev.keader.correiostracker.home.HomeFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val homeFragment = HomeFragment()
        val archivedFragment = ArchivedFragment()
        makeCurrentFragment(homeFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {item ->
            when(item.itemId) {
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_archived -> makeCurrentFragment(archivedFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}
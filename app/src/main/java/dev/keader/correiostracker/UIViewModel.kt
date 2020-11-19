package dev.keader.correiostracker

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch

class UIViewModel @ViewModelInject constructor(private val repository: TrackingRepository) : ViewModel() {

    private val _bottomNavVisibility = MutableLiveData(View.VISIBLE)
    val bottomNavVisibility: LiveData<Int>
        get() = _bottomNavVisibility

    init {
        viewModelScope.launch {
            repository.refreshTracks()
        }
    }

    fun setBottomNavVisibility(visibility: Int) {
        _bottomNavVisibility.value = visibility
    }
}

package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.sicnu.cs.hln.jingshiai.data.local.JingShiDatabase
import cn.edu.sicnu.cs.hln.jingshiai.data.model.Stats
import cn.edu.sicnu.cs.hln.jingshiai.data.repository.JingShiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// 首页统计：直接观察 Room 中的真实数据
class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JingShiRepository(JingShiDatabase.getInstance(application))

    val stats: StateFlow<Stats> = repository.observeStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Stats()
        )
}

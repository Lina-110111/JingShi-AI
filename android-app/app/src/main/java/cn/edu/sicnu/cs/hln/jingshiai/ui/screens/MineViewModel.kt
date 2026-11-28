package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.sicnu.cs.hln.jingshiai.data.local.JingShiDatabase
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.UserEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.model.Stats
import cn.edu.sicnu.cs.hln.jingshiai.data.repository.JingShiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// “我的”页面：统计数据 + 本地用户资料
class MineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JingShiRepository(JingShiDatabase.getInstance(application))

    val stats: StateFlow<Stats> = repository.observeStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Stats()
        )

    val user: StateFlow<UserEntity?> = repository.observeUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}

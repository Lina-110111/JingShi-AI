package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 首页与“我的”共用的统计数据
data class StatsUiState(
    val recognitionCount: Int = 0,
    val gameCount: Int = 0
)

class StatsViewModel : ViewModel() {

    // 尚未接入 Room，先提供明确的空状态，不虚构统计数字；
    // 接入数据库后，把这里替换为 Repository 暴露的 Flow 即可。
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()
}

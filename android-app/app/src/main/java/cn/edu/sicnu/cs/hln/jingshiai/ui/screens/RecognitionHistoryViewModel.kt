package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.sicnu.cs.hln.jingshiai.data.local.JingShiDatabase
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.RecognitionEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.repository.JingShiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// 识别历史：观察 Room 中按时间倒序的记录
class RecognitionHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JingShiRepository(JingShiDatabase.getInstance(application))

    val records: StateFlow<List<RecognitionEntity>> = repository.observeRecognitionHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}

package com.qudoos.myapp

import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {
    private val _historyItems = mutableListOf<HistoryItem>()
    val historyItems: List<HistoryItem> = _historyItems.toList()

    fun addHistoryItem(historyItem: HistoryItem) {
        _historyItems.add(historyItem)
    }
}
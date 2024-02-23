package com.qudoos.myapp

import java.util.Date

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val timestamp: String = Date().toString(),
    val dateTime: Any
)
val quizResults = mutableListOf<QuizResult>()

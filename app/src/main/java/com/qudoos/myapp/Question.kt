package com.qudoos.myapp

data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)
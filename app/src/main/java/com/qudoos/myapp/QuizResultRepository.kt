package com.qudoos.myapp

import android.content.Context
import com.google.gson.Gson

object QuizResultRepository {

    fun saveQuizResult(context: Context, quizResult: QuizResult) {
        val sharedPreferences = context.getSharedPreferences("QuizResults", Context.MODE_PRIVATE)
        val gson = Gson()
        val quizResultsJson = sharedPreferences.getString("quizResults", "[]")
        val quizResults = gson.fromJson(quizResultsJson, Array<QuizResult>::class.java).toMutableList()
        quizResults.add(quizResult)
        val editor = sharedPreferences.edit()
        editor.putString("quizResults", gson.toJson(quizResults))
        editor.apply()
    }

    fun getQuizResults(context: Context): List<QuizResult> {
        val sharedPreferences = context.getSharedPreferences("QuizResults", Context.MODE_PRIVATE)
        val gson = Gson()
        val quizResultsJson = sharedPreferences.getString("quizResults", "[]")
        return gson.fromJson(quizResultsJson, Array<QuizResult>::class.java).toList()
    }
}


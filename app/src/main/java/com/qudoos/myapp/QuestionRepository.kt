package com.qudoos.myapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object QuestionRepository {


    private const val QUESTION_PREFS_KEY = "quiz_questions"


    fun saveQuizQuestion(context: Context, quizQuestion: QuizQuestion) {
        val sharedPreferences = context.getSharedPreferences("QuizQuestions", Context.MODE_PRIVATE)
        val gson = Gson()
        val questionsJson = sharedPreferences.getString(QUESTION_PREFS_KEY, "[]")
        val questions = gson.fromJson(questionsJson, Array<QuizQuestion>::class.java).toMutableList()
        questions.add(quizQuestion)
        val editor = sharedPreferences.edit()
        editor.putString(QUESTION_PREFS_KEY, gson.toJson(questions))
        editor.apply()
    }

    fun getQuizQuestions(context: Context): List<QuizQuestion> {
        val sharedPreferences = context.getSharedPreferences("QuizQuestions", Context.MODE_PRIVATE)
        val gson = Gson()
        val questionsJson = sharedPreferences.getString(QUESTION_PREFS_KEY, "[]")
        return gson.fromJson(questionsJson, Array<QuizQuestion>::class.java).toList()
    }

    fun deleteQuizQuestion(context: Context, quizQuestion: QuizQuestion) {
        val sharedPreferences = context.getSharedPreferences("QuizQuestions", Context.MODE_PRIVATE)
        val gson = Gson()
        val quizQuestionsJson = sharedPreferences.getString("quizQuestions", "[]")
        val quizQuestions = gson.fromJson(quizQuestionsJson, Array<QuizQuestion>::class.java).toMutableList()
        quizQuestions.remove(quizQuestion)
        val editor = sharedPreferences.edit()
        editor.putString("quizQuestions", gson.toJson(quizQuestions))
        editor.apply()
    }
}


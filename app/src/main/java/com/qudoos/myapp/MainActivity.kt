package com.qudoos.myapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.qudoos.myapp.QuizResultRepository.getQuizResults
import com.qudoos.myapp.QuizResultRepository.saveQuizResult
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.jvm.internal.*



class MainActivity : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

//NAVDRAWER E NAVCONTROLLER
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MyApp() {
    MyAppTheme {


    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val quizResults = remember { mutableStateListOf<QuizResult>() }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },

            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = listOf(
                    MenuItem(
                        id = "home",
                        title = "Home",
                        contentDescription = "Go to home screen",
                        icon = Icons.Default.Home
                    )
                ),
                onItemClick = {
                    navController.navigate("Home")
                },
            )
        }
    ) {
        NavHost(navController = navController, startDestination = "Home") {
            composable("Home") { FirstScreen(navController) }
            composable("QuizScreen") {
                QuizScreen(navController) { totalQuestions, correctAnswers, wrongAnswers ->
                    val currentDateTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formattedDateTime = currentDateTime.format(formatter)
                    val result = QuizResult(
                        totalQuestions,
                        correctAnswers,
                        wrongAnswers,
                        formattedDateTime,
                        getCurrentDateTime()
                    )
                    quizResults.add(result)
                    navController.navigate("ResultScreen/$totalQuestions/$correctAnswers/$wrongAnswers")
                }
            }

            composable("ResultScreen/{totalQuestions}/{correctAnswers}/{wrongAnswers}") { backStackEntry ->
                val totalQuestions =
                    backStackEntry.arguments?.getString("totalQuestions")?.toInt() ?: 0
                val correctAnswers =
                    backStackEntry.arguments?.getString("correctAnswers")?.toInt() ?: 0
                val wrongAnswers = backStackEntry.arguments?.getString("wrongAnswers")?.toInt() ?: 0
                ResultScreen(
                    totalQuestions = totalQuestions,
                    correctAnswers = correctAnswers,
                    wrongAnswers = wrongAnswers,
                    onRetry = { navController.navigate("QuizScreen") },
                    onGoToHome = { navController.navigate("Home") },
                    navController = navController
                )
            }
            composable("AddQuestionsScreen") {
                AddQuestionScreen(
                    context = LocalContext.current,
                    onAddQuestion = { /* Pass your implementation here */ }
                )
            }

            composable("QuestionListScreen") {
                val context = LocalContext.current
                QuestionListScreen(context = context, navController = navController)
            }

            composable("HistoryScreen") {
                HistoryScreen(quizResults = quizResults,navController = navController)
            }
        }

    }
}
}


//PAGINA PRINCIPALE INTERFACCIA UTENTE
        @Composable
fun FirstScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(66.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("QuizScreen") },
            modifier = Modifier
                .height(150.dp)
                .width(350.dp)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Go to Quiz Screen",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.1.sp
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("HistoryScreen") },
                    modifier = Modifier
                        .height(150.dp)
                        .width(120.dp)
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "History",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.1.sp
                        )
                    )
                }

                Button(
                    onClick = { navController.navigate("QuestionListScreen") },
                    modifier = Modifier
                        .height(150.dp)
                        .width(120.dp)
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Questions",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.1.sp
                        )
                    )
                }
            }
        }


                    }
                }




//pigna della quiz
@Composable
fun QuizScreen(navController: NavHostController, navigateToResult: (Int, Int, Int) -> Unit) {
    // Retrieve quiz questions from the QuestionRepository
    val context = LocalContext.current
    val quizQuestions = remember { QuestionRepository.getQuizQuestions(context) }


    var currentQuestionIndex by remember { mutableStateOf(0) }
    var correctAnswers by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }

    // Validate if there are questions available
    if (quizQuestions.isEmpty()) {
        Text(text = "No questions available")
        return
    }

    val currentQuestion = quizQuestions[currentQuestionIndex]

    Column(
        modifier = Modifier
            .padding(36.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .clip(shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = currentQuestion.question,
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display answer options
        currentQuestion.options.forEach { option ->
            OptionButton(
                text = option,
                onClick = {
                    selectedAnswer = option
                    if (selectedAnswer == currentQuestion.correctAnswer) {
                        correctAnswers++
                    } else {
                        wrongAnswers++
                    }
                    if (currentQuestionIndex < quizQuestions.size - 1) {
                        currentQuestionIndex++
                        selectedAnswer = ""
                    } else {
                        navigateToResult(quizQuestions.size, correctAnswers, wrongAnswers)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FloatingActionButton(
                onClick = {
                    if (currentQuestionIndex < quizQuestions.size - 1) {
                        currentQuestionIndex++
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Skip")
            }

            FloatingActionButton(
                onClick = {
                    navigateToResult(quizQuestions.size, correctAnswers, wrongAnswers)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Terminate")
            }
        }
    }
}



fun getCurrentDateTime(): String {
    val currentTime = Calendar.getInstance().time
    return currentTime.toString()
}

@Composable
fun OptionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
    }
}
//RISULTATO DEL QUIZ
@Composable
fun ResultScreen(
    totalQuestions: Int,
    correctAnswers: Int,
    wrongAnswers: Int,
    onRetry: () -> Unit,
    onGoToHome: () -> Unit,
    navController: NavHostController
) {
    val result = QuizResult(
        totalQuestions,
        correctAnswers,
        wrongAnswers,
        dateTime = getCurrentDateTime()
    )
    quizResults.add(result)

// Save quiz result
    saveQuizResult(context = LocalContext.current, quizResult = result)


    Column(
        modifier = Modifier
            .padding(36.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
            .clip(shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = "Result",
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total Questions: $totalQuestions",
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )

        Text(
            text = "Correct Answers: $correctAnswers",
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )

        Text(
            text = "Wrong Answers: $wrongAnswers",
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onRetry() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Retry")
        }
        Button(
            onClick = { onGoToHome() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Home")
        }


        Button(
            onClick = {
                navController.navigate("HistoryScreen")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("View History")
        }
    }
}

//CRONOLOGIA UTENTI DEI QUIZ
@Composable
fun HistoryScreen(quizResults: List<QuizResult>, navController: NavHostController) {
    rememberNavController()
    val context = LocalContext.current

    // Get quiz results from device storage
    val quizResults = getQuizResults(context)
    Column(
        modifier = Modifier
            .padding(36.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .wrapContentSize(Alignment.TopCenter)
            .clip(shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = "Quiz Results History",
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(quizResults) { result ->
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Date & Time: ")
                        }
                        append(result.timestamp.toString())
                        append("\n")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Total Questions: ")
                        }
                        append(result.totalQuestions.toString())
                        append(", ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Correct Answers: ")
                        }
                        append(result.correctAnswers.toString())
                        append(", ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Wrong Answers: ")
                        }
                        append(result.wrongAnswers.toString())
                    },
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1
                )
                Divider(color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Go Back")
        }
    }
}


@Composable
fun AddQuestionScreen(context: Context, onAddQuestion: (QuizQuestion) -> Unit)  {
        var question by remember { mutableStateOf("") }
        var option1 by remember { mutableStateOf("") }
        var option2 by remember { mutableStateOf("") }
        var option3 by remember { mutableStateOf("") }
        var correctAnswer by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = option1,
                onValueChange = { option1 = it },
                label = { Text("Option 1") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = option2,
                onValueChange = { option2 = it },
                label = { Text("Option 2") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = option3,
                onValueChange = { option3 = it },
                label = { Text("Option 3") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = correctAnswer,
                onValueChange = { correctAnswer = it },
                label = { Text("Correct Answer") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                  //  val context = LocalContext.current
                    val options = listOf(option1, option2, option3)
                    val quizQuestion = QuizQuestion(question, options, correctAnswer)
                    onAddQuestion(quizQuestion)
                    saveQuizQuestion(context, quizQuestion) // Save the question
                    // Optionally, you can show a toast message indicating that the question has been added
                  //  showToast("Question added successfully")
                    // You can also reset the input fields after saving the question if needed
                    question = ""
                    option1 = ""
                    option2 = ""
                    option3 = ""
                    correctAnswer = ""
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Question")
            }

        }
    }



    fun saveQuizQuestion(context: Context, quizQuestion: QuizQuestion) {
        QuestionRepository.saveQuizQuestion(context, quizQuestion)
    }

@Composable
fun QuestionListScreen(context: Context, navController: NavController) {
    val quizQuestions = remember { QuestionRepository.getQuizQuestions(context) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(quizQuestions) { question ->
                QuizQuestionItem(quizQuestion = question)
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("AddQuestionsScreen") },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Question"
            )
        }
    }
}






@Composable
fun QuizQuestionItem(quizQuestion: QuizQuestion) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = quizQuestion.question,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            quizQuestion.options.forEach { option ->
                Text(
                    text = option,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                text = "Correct Answer: ${quizQuestion.correctAnswer}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}








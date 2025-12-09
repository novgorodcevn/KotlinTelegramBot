package org.example

import org.example.data.Word
import java.io.File

fun main() {

    val dictionary = loadDictionary()
    val learnedCount = dictionary.filter { it.correctAnswersCount >= MIN_CORRECT_COUNT }.size

    while (true) {
        println(
            """
            Меню: 
            1 – Учить слова
            2 – Статистика
            0 – Выход
        """.trimIndent()
        )
        val userInput = readln()

        when (userInput) {
            "1" -> while (true) {
                val notLearnedList = dictionary.filter { it.correctAnswersCount < MIN_CORRECT_COUNT }
                if (notLearnedList.isEmpty()) {
                    println("Все слова в словаре выучены")
                    break
                }

                val questionWords = notLearnedList.take(NUMBER_UNLEARNED_WORDS).shuffled().toMutableList()
                if (questionWords.size < NUMBER_UNLEARNED_WORDS) {
                    val numberAddWords = NUMBER_UNLEARNED_WORDS - questionWords.size
                    questionWords.addAll(dictionary.filter {
                        it.correctAnswersCount >= MIN_CORRECT_COUNT
                                && it !in notLearnedList
                    }.shuffled().take(numberAddWords))
                }
                val correctAnswer = questionWords.random()

                println()
                println("${correctAnswer.original}:")
                questionWords.mapIndexed { index, word ->
                    println("${index + 1} - ${word.translate}")
                }
                val userResponse = readln().toIntOrNull()
            }

            "2" -> println(
                "Статистика\nВыучено $learnedCount из" +
                        " ${dictionary.size} слов |" +
                        " ${(learnedCount / dictionary.size) * 100}%\n"
            )

            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun loadDictionary(): List<Word> {
    val wordsFile = File("words.txt")
    val dictionary = mutableListOf<Word>()
    if (wordsFile.exists()) {

        for (line in wordsFile.readLines()) {
            val parts = line.split("|")
            val word = Word(
                original = parts[0],
                translate = parts[1],
                correctAnswersCount = parts.getOrNull(2)?.toIntOrNull() ?: 0
            )
            dictionary.add(word)
        }
    }
    return dictionary
}

const val MIN_CORRECT_COUNT = 3
const val NUMBER_UNLEARNED_WORDS = 4
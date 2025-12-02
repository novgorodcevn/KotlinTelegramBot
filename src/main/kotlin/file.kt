package org.example

import org.example.data.Word
import java.io.File

fun main() {

    val dictionary = loadDictionary()
    val learnedCount = dictionary.filter { it.correctAnswersCount >= 3 }.size
    val percent = (learnedCount.toDouble() / dictionary.size) * 100
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
            "1" -> println("Учить слова")
            "2" -> println("Статистика\nВыучено $learnedCount из ${dictionary.size} слов | ${percent.toInt()}%\n")
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
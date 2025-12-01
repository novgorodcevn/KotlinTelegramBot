package org.example

import org.example.data.Word
import java.io.File

fun main() {

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
        dictionary.forEach {
            println(it)
        }
    } else {
        println("Файла не существует")
    }
}
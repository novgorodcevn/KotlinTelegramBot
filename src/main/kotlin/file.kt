package org.example

import java.io.File

fun main() {

    val wordsFile = File("words.txt")
    if (wordsFile.exists()) {
        for (line in wordsFile.readLines()) {
            println(line)
        }
    } else {
        println("Файла не существует")
    }
}
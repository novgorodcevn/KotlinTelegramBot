package org.example

import java.io.File

fun main() {

    val wordsFile = File("words.txt")
    // wordsFile.createNewFile()
    for (i in wordsFile.readLines()) {
        println(i)
    }
}
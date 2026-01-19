package org.example

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)

data class Statistics(
    val learnedCount: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    var question: Question? = null
    val dictionary = loadDictionary().toMutableList()

    fun loadDictionary(): List<Word> {
        try {
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
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("некорректный файл")
        }
    }

    fun saveDictionary(words: List<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        words.forEach { word ->
            wordsFile.appendText("${word.original}|${word.translate}|${word.correctAnswersCount}\n")
        }
    }

    fun getStatistics(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswersCount >= MIN_CORRECT_COUNT }.size
        val total = dictionary.size
        val percent = (learnedCount * 100) / total
        return Statistics(learnedCount, total, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < MIN_CORRECT_COUNT }
        if (notLearnedList.isEmpty()) return null
        val questionWords = notLearnedList.shuffled().take(NUMBER_UNLEARNED_WORDS).toMutableList()
        if (questionWords.size < NUMBER_UNLEARNED_WORDS) {
            val numberAddWords = NUMBER_UNLEARNED_WORDS - questionWords.size
            questionWords.addAll(dictionary.filter {
                it.correctAnswersCount >= MIN_CORRECT_COUNT
                        && it !in notLearnedList
            }.shuffled().take(numberAddWords))
        }
        val correctAnswer = questionWords.random()
        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerId: Int): Boolean {
        return question?.let { it ->
            val correctAnswerId = it.variants.indexOfFirst { it.original == question?.correctAnswer?.original }
            if (userAnswerId == correctAnswerId) {
                val index = dictionary.indexOfFirst { it.original == question?.correctAnswer?.original }
                val updatedWord = dictionary[index]
                dictionary[index] =
                    updatedWord.copy(correctAnswersCount = updatedWord.correctAnswersCount + 1)
                saveDictionary(dictionary)
                return true
            } else {
                return false
            }
        } ?: false
    }
}

const val MIN_CORRECT_COUNT = 3
const val NUMBER_UNLEARNED_WORDS = 4
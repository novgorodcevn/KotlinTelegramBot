package org.example

import org.example.data.Word
import java.io.File

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
    private var question: Question? = null


    val dictionary = loadDictionary().toMutableList()

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

    fun saveDictionary(dictionary: MutableList<Word>) {
        val saveDictionary = dictionary.toMutableList()
    }

    fun getStatistics(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswersCount >= MIN_CORRECT_COUNT }.size
        val total = dictionary.size
        val percent = (learnedCount / total) * 100
        return Statistics(learnedCount, total, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < MIN_CORRECT_COUNT }
        if (notLearnedList.isEmpty()) return null
        val questionWords = notLearnedList.take(NUMBER_UNLEARNED_WORDS).shuffled().toMutableList()
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
                val updatedWorld = dictionary[index]
                dictionary[index] =
                    updatedWorld.copy(correctAnswersCount = updatedWorld.correctAnswersCount + 1)
                saveDictionary(dictionary)
                return true
            } else {
                return false
            }
        } ?: false
    }
}
package com.example.demo

import org.example.Question
import org.example.Word
import org.example.asConsoleString
import kotlin.test.Test
import kotlin.test.assertEquals

class QuestionExtensionTest {

    @Test
    fun asConsoleStringCaseWith4Options() {
        val word1 = Word(original = "Apple", translate = "Яблоко")
        val word2 = Word(original = "Banana", translate = "Банан")
        val word3 = Word(original = "Orange", translate = "Апельсин")
        val word4 = Word(original = "Pear", translate = "Груша")

        val question = Question(
            variants = listOf(word1, word2, word3, word4),
            correctAnswer = word1
        )
        val result = question.asConsoleString()
        val expected = """
            Apple
            1 - Яблоко
            2 - Банан
            3 - Апельсин
            4 - Груша
            ----------
            0 - Меню
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun asConsoleStringReflectsChangedVariants() {
        val word1 = Word(original = "Apple", translate = "Яблоко")
        val word2 = Word(original = "Banana", translate = "Банан")
        val word3 = Word(original = "Orange", translate = "Апельсин")
        val word4 = Word(original = "Pear", translate = "Груша")

        val question = Question(
            variants = listOf(word2, word4, word1, word3),
            correctAnswer = word1
        )
        val result = question.asConsoleString()
        val expected = """
            Apple
            1 - Банан
            2 - Груша
            3 - Яблоко
            4 - Апельсин
            ----------
            0 - Меню
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun asConsoleStringEmptyVariants() {
        val word1 = Word(original = "Apple", translate = "Яблоко")
        val question = Question(
            variants = emptyList(),
            correctAnswer = word1
        )
        val result = question.asConsoleString()
        val expected = """
            Apple
         
            ----------
            0 - Меню
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun asConsoleStringHandles10Variants() {
        val variants = (1..200).map { num ->
            Word(original = "$num", translate = "Ответ $num")
        }
        val correctAnswer = Word(original = "Apple", translate = "Яблоко")

        val question = Question(
            variants = variants,
            correctAnswer = correctAnswer
        )

        val variantsString = variants.take(10).mapIndexed { index, word ->
            "${index + 1} - ${word.translate}"
        }.joinToString("\n")

        val expected = listOf(
            "Apple",
            variantsString,
            "----------",
            "0 - Меню"
        ).joinToString("\n")

        val result = question.asConsoleString()
        assertEquals(expected, result)
    }
}
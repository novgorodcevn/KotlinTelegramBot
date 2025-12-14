package org.example

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word -> "${index + 1} - ${word.translate}" }
        .joinToString("\n")
    return this.correctAnswer.original + "\n" + variants + "\n" + "----------" + "\n" + "0 - Меню"
}

fun main() {

    val trainer = LearnWordsTrainer()

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
                val question = trainer.getNextQuestion()
                if (question == null) {
                    println("Все слова в словаре выучены")
                    break
                }
                println()
                println(question.asConsoleString())

                when (val userAnswerInput = readln().toIntOrNull()) {
                    null -> println("Не коррекный ввод")
                    0 -> break

                    in 1..4 -> {
                        if (trainer.checkAnswer(userAnswerInput.minus(1))) {
                            println("Правильно!")

                        } else {
                            println("Неправельно!${question.correctAnswer.original} - это ${question.correctAnswer.translate}")
                        }
                    }
                    else -> {
                        println("Неверное значение: ${userAnswerInput}. Введите число от 0 до 4.")
                    }
                }
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println(
                    "Статистика\nВыучено ${statistics.learnedCount} из" +
                            " ${statistics.total} слов |" +
                            " ${statistics.percent}%\n"
                )
            }

            "0" -> return
            else -> println("Введите число 1, 2 или 0")

        }
    }
}

const val MIN_CORRECT_COUNT = 3
const val NUMBER_UNLEARNED_WORDS = 4
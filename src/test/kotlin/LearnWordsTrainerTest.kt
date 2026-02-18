import org.example.LearnWordsTrainer
import org.example.NUMBER_UNLEARNED_WORDS
import org.example.Statistics
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.Test

class LearnWordsTrainerTest {

    @Test
    fun `test statistics with 4 words of 7`() {
        val trainer = LearnWordsTrainer("src/test/4_words_of_7.txt")
        kotlin.test.assertEquals(
            Statistics(learnedCount = 4, total = 7, percent = 57),
            trainer.getStatistics()
        )
    }

    @Test
    fun `test statistics with corrupted file`() {
        val exception = assertThrows<IllegalStateException> {
            LearnWordsTrainer("src/test/words_corrupted.txt")
        }
        assertEquals("некорректный файл", exception.message)
    }

    @Test
    fun `fun test getNextQuestion() with 1 unlearned word`() {
        val trainer = LearnWordsTrainer("src/test/1_unlearned_word.txt")
        kotlin.test.assertEquals(
            NUMBER_UNLEARNED_WORDS,
            trainer.getNextQuestion()?.variants?.size
        )
    }

    @Test
    fun `test getNextQuestion() with all words learned`() {
        val trainer = LearnWordsTrainer("src/test/4_words_of_7.txt")
        kotlin.test.assertEquals(
            null,
            trainer.getNextQuestion()
        )
    }

    @Test
    fun `test checkAnswer() with true`() {
        val trainer = LearnWordsTrainer("src/test/4_words_of_7.txt")
        val question = trainer.getNextQuestion()
        trainer.question = question
        val index = trainer.question?.correctAnswer?.original
        val correctAnswerIndex = trainer.question?.variants?.indexOfFirst { it.original == index }
        val result = trainer.checkAnswer(correctAnswerIndex)
        kotlin.test.assertEquals(
            true,
            result
        )
    }

    @Test
    fun `test checkAnswer() with false`() {
        val trainer = LearnWordsTrainer("src/test/4_words_of_7.txt")
        val question = trainer.getNextQuestion()
        trainer.question = question
        val index = trainer.question?.correctAnswer?.original
        val correctAnswerIndex = trainer.question?.variants?.indexOfFirst { it.original != index }
        val result = trainer.checkAnswer(correctAnswerIndex)
        kotlin.test.assertEquals(
            false,
            result
        )
    }

    @Test
    fun `test resetProgress() with 2 words in dictionary`() {
        val trainer = LearnWordsTrainer("src/test/reset_word.txt")
        trainer.resetProgress()
        val allZero = trainer.dictionary.all { it.correctAnswersCount == 0 }
        assertTrue(
            allZero
            )
    }
}

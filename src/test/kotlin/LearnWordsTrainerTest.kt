import org.example.LearnWordsTrainer
import org.example.NUMBER_UNLEARNED_WORDS
import org.example.Statistics
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
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
            LearnWordsTrainer("src/test/4_words_of_7.txt")
        }
        assertEquals("некорректный файл", exception.message)
    }

    @Test
    fun `fun test getNextQuestion() with 1 unleard word`() {
        val trainer = LearnWordsTrainer("src/test/4_words_of_7.txt")
        kotlin.test.assertEquals(
            NUMBER_UNLEARNED_WORDS,
            trainer.getNextQuestion()?.variants?.size
        )
    }

    @Test
    fun `test getNextQuestion() with all words learned`()  {
        val trainer = LearnWordsTrainer("src/test/4_words_of_7.txt")
        kotlin.test.assertEquals(
            null,
            trainer.getNextQuestion()?.variants?.size
        )
    }

    @Test
    fun `test checkAnswer() with true`() {
        val trainer = LearnWordsTrainer("src/test/4_words_of_7.txt")
        val correctAnswerId = trainer.getNextQuestion()?.variants?.indexOfFirst { it.original == trainer.question?.correctAnswer?.original }
        kotlin.test.assertEquals(
            true,
            trainer.checkAnswer(correctAnswerId)
        )
    }

}

package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replayMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun main(args: Array<String>) {

    val botToken = args[0]
    var lastUpdateId = 0L
    val telegramBotService = TelegramBotService()
    val trainer = LearnWordsTrainer()

    val json = Json {
        ignoreUnknownKeys = true
    }

    fun checkNextQuestionAndSend(
        trainer: LearnWordsTrainer,
        telegramBotService: TelegramBotService,
        botToken: String,
        chatId: Long
    ) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            telegramBotService.sendMessage(json, botToken, chatId, "Вы выучили все слова в базе")
        } else {
            telegramBotService.sendQuestion(json, botToken, chatId, question)
        }
    }

    while (true) {
        Thread.sleep(2000)
        val responseString: String = telegramBotService.getUpdates(botToken, lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val message = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data

        if (chatId != null && message == "/start") {
            telegramBotService.sendMenu(json, botToken, chatId)
        }

        if (data != null && chatId != null) {

            when (data) {

                CALLBACK_DATA_LEARN_WORDS -> checkNextQuestionAndSend(trainer, telegramBotService, botToken, chatId)

                CALLBACK_DATA_STATISTICS -> {
                    val statistics = trainer.getStatistics()
                    telegramBotService.sendMessage(
                        json,
                        botToken,
                        chatId,
                        "Статистика\nВыучено ${statistics.learnedCount} из" +
                                " ${statistics.total} слов |" +
                                " ${statistics.percent}%\n"
                    )
                }

                CALLBACK_DATA_EXIT -> telegramBotService.sendMessage(
                    json,
                    botToken,
                    chatId,
                    "Выбрано выход"
                )
            }

            if (data.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
                val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()

                if (userAnswerIndex != null) {

                    if (trainer.checkAnswer(userAnswerIndex)) {
                        telegramBotService.sendMessage(json, botToken, chatId, "Правильно!")
                    } else {
                        telegramBotService.sendMessage(
                            json,
                            botToken,
                            chatId,
                            "Неправильно! ${trainer.question?.correctAnswer?.original} - это ${trainer.question?.correctAnswer?.translate}"
                        )
                    }
                    checkNextQuestionAndSend(trainer, telegramBotService, botToken, chatId)
                }
            }
        }
    }
}
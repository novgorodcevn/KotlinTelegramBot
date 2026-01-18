package org.example


fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService()
    val trainer = LearnWordsTrainer()

    val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(botToken, updateId)
        println(updates)
        val matchResultUpdate: MatchResult? = updateIdRegex.find(updates)
        val groupsUpdate = matchResultUpdate?.groups
        val updateIdValue = groupsUpdate?.get(1)?.value

        updateId = updateIdValue?.toInt()?.plus(1) ?: continue

        val mathResultChat: MatchResult? = chatIdRegex.find(updates)
        val groupsChat = mathResultChat?.groups
        val chatIdValue = groupsChat?.get(1)?.value
        val chatId = chatIdValue?.toInt()

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value

        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        fun checkNextQuestionAndSend(
            trainer: LearnWordsTrainer,
            telegramBotService: TelegramBotService,
            chatId: Int
        ) {

                val question = trainer.getNextQuestion()

                if (question == null) {
                    telegramBotService.sendMessage(botToken, chatId, "Вы выучили все слова в базе")
                } else {
                    telegramBotService.sendQuestion(botToken, chatId, question)
                }
                if (data != null) {
                    if (data.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
                        val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()

                        if (userAnswerIndex != null) {

                            if (trainer.checkAnswer(userAnswerIndex)) {
                                telegramBotService.sendMessage(botToken, chatId, "Правильно!")
                            } else {
                                telegramBotService.sendMessage(
                                    botToken,
                                    chatId,
                                    "Неправильно! ${question?.correctAnswer?.original} - это ${question?.correctAnswer?.translate}"
                                )
                            }
                            checkNextQuestionAndSend(trainer, telegramBotService, chatId)
                        }
                    }
                }

        }

        if (chatId != null && text == "/start") {
            telegramBotService.sendMenu(botToken, chatId)
        }

        if (data != null && chatId != null) {
            val statistics = trainer.getStatistics()

            when (data) {
                CALLBACK_DATA_LEARN_WORDS -> {
                    checkNextQuestionAndSend(trainer, telegramBotService, chatId)
                }
                CALLBACK_DATA_STATISTICS -> telegramBotService.sendMessage(
                    botToken,
                    chatId,
                    "Статистика\nВыучено ${statistics.learnedCount} из" +
                            " ${statistics.total} слов |" +
                            " ${statistics.percent}%\n"
                )
                CALLBACK_DATA_EXIT -> telegramBotService.sendMessage(
                    botToken,
                    chatId,
                    "Выбрано выход"
                )
            }
        }
    }
}
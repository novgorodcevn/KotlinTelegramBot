package org.example


fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService()

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(botToken, updateId)
        println(updates)

        val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
        val matchResultUpdate: MatchResult? = updateIdRegex.find(updates)
        val groupsUpdate = matchResultUpdate?.groups
        val updateIdValue = groupsUpdate?.get(1)?.value
        println(updateIdValue)
        updateId = updateIdValue?.toInt()?.plus(1) ?: continue

        val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+),".toRegex()
        val mathResultChat: MatchResult? = chatIdRegex.find(updates)
        val groupsChat = mathResultChat?.groups
        val chatIdValue = groupsChat?.get(1)?.value
        val chatId = chatIdValue?.toInt()

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value

        if (chatId != null && text != null) {
            telegramBotService.sendMessage(botToken, chatId, text)
        }
    }
}
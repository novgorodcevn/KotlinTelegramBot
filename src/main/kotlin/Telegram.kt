package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


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
        val textUpdate = groupsUpdate?.get(1)?.value
        println(textUpdate)
        updateId = textUpdate?.toInt()?.plus(1) ?: continue

        val chatTextRegex: Regex = "\"chat\":\\{\"id\":(.+?),".toRegex()
        val mathResultChat: MatchResult? = chatTextRegex.find(updates)
        val groupsChat = mathResultChat?.groups
        val textChatId = groupsChat?.get(1)?.value
        val chatId = textChatId?.toInt()
        println(textChatId)

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

        if (chatId != null && text != null) {
            telegramBotService.sendMessage(botToken, chatId, text)
        }
    }
}

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"
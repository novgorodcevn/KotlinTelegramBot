package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val updateTextRegex: Regex = "\"update_id\":(.+?),".toRegex()
        val mathResultUpdate: MatchResult? = updateTextRegex.find(updates)
        val groupsUpdate = mathResultUpdate?.groups
        val textUpdate = groupsUpdate?.get(1)?.value
        updateId = textUpdate?.toInt()?.plus(1) ?: continue

        val chatTextRegex: Regex = "\"chat\":\\{\"id\":(.+?),".toRegex()
        val mathResultChat: MatchResult? = chatTextRegex.find(updates)
        val groupsChat = mathResultChat?.groups
        val textChatId = groupsChat?.get(1)?.value
        val chatId = textChatId?.toInt()
        println(textChatId)

        val textRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = textRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)
        if (chatId != null && text != null) {
            sendMessage(botToken,chatId,text)
        }
    }

}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}

fun sendMessage(botToken: String, chatId: Int, text: String): String? {
    val urlSendMessage = "$TELEGRAM_BASE_URL$botToken/sendMessage?chat_id=$chatId&text=$text"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"
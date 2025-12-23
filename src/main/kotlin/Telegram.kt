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

        val messageTextRegex: Regex = "\"update_id\":(.+?),".toRegex()
        val mathResult: MatchResult? = messageTextRegex.find(updates)
        val groups = mathResult?.groups
        val text = groups?.get(1)?.value
        updateId = text?.toInt()?.plus(1) ?: continue
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"
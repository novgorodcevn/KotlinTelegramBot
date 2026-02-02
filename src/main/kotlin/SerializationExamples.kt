package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json



fun main() {

    val json = Json {
        ignoreUnknownKeys = true
    }

    val responseString = """
        {
            "ok": true,
            "result": [
                {
                    "update_id": 389997207,
                    "message": {
                        "message_id": 363,
                        "from": {
                            "id": 850586374,
                            "is_bot": false,
                            "first_name": "Nik",
                            "username": "VldNovgor",
                            "language_code": "ru"
                        },
                        "chat": {
                            "id": 850586374,
                            "first_name": "Nik",
                            "username": "VldNovgor",
                            "type": "private"
                        },
                        "date": 1769974282,
                        "text": "/start",
                        "entities": [
                            {
                                "offset": 0,
                                "length": 6,
                                "type": "bot_command"
                            }
                        ]
                    }
                }
            ]
        }
    """.trimIndent()

    val response = json.decodeFromString<Response>(responseString)
    println(response)
}
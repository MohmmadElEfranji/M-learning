package com.nerds.m_learning.student.model

import java.util.*

data class TextMessage(
    var message: String? = null,
    var timeOfMessage: Date? = Date(),
    var senderID: String? = ""
)

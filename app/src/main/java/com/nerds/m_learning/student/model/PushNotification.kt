package com.nerds.m_learning.student.model

import com.nerds.m_learning.student.model.NotificationData

data class PushNotification(
    val data: NotificationData,
    val to: String
)

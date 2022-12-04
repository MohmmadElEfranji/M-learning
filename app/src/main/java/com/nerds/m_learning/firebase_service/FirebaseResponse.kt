package com.nerds.m_learning.firebase_service

sealed class FirebaseResponse<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : FirebaseResponse<T>(data)
    class Failure<T>(data: T? = null, message: String? = null) :
        FirebaseResponse<T>(data, message)
}
package com.example.uniregnative.data

/**
 * A registered student account, created through the Signup screen and
 * checked against on Login. Held in-memory in MainActivity for this
 * milestone (no backend/database yet — see report for justification).
 */
data class Account(
    val fullName: String,
    val studentId: String,
    val email: String,
    val faculty: String,
    val password: String,
)
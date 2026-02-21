package com.example.embed.data

/**
 * Topic domains covered by the question bank.
 * Each question belongs to exactly one domain, allowing the user to
 * filter sessions by topic in Settings.
 */
enum class Domain(val displayName: String) {
    MEMORY("Memory"),
    INTERRUPTS("Interrupts"),
    PROTOCOLS("Communication Protocols"),
    RTOS("RTOS Concepts"),
    POWER("Power Management"),
    FIRMWARE("Firmware & Code")
}
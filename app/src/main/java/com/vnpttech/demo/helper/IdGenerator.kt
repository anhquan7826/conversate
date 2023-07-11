package com.vnpttech.demo.helper

import java.util.UUID

object IdGenerator {
    fun generateDocumentId(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString().replace("-", "")
    }
}
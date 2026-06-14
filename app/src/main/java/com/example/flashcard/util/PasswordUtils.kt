package com.example.flashcard.util

import java.security.MessageDigest

/**
 * Tiện ích hash mật khẩu bằng SHA-256.
 * Mật khẩu KHÔNG BAO GIỜ được lưu dạng plain text trong CSDL.
 */
object PasswordUtils {

    /**
     * Hash mật khẩu bằng thuật toán SHA-256.
     * @param password mật khẩu dạng plain text
     * @return chuỗi hex đã hash
     */
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * So sánh mật khẩu plain text với hash đã lưu.
     * @param plainPassword mật khẩu người dùng nhập
     * @param hashedPassword hash đã lưu trong CSDL
     * @return true nếu khớp
     */
    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return hashPassword(plainPassword) == hashedPassword
    }
}

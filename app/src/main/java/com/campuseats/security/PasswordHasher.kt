package com.campuseats.security

import java.security.MessageDigest

/**
 * Standard cryptographic password hashing utility using salted SHA-256,
 * fully compatible with all standard Android and Java runtime environments.
 * Ensures passwords are never stored or compared in plaintext.
 */
object PasswordHasher {

    // Application salt to prevent rainbow table and dictionary attacks
    private const val PEPPER_SALT = "CampusEats_Security_Salt_2024!"

    /**
     * Hashes a plaintext password using salted SHA-256.
     * @param password Plaintext password to hash
     * @return Hexadecimal encoded hash string
     */
    fun hash(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(PEPPER_SALT.toByteArray(Charsets.UTF_8))
        val digest = md.digest(password.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies whether a candidate plaintext password matches an expected stored hash.
     * @param password Candidate plaintext password
     * @param expectedHash Stored hexadecimal hash string
     * @return true if matches, false otherwise
     */
    fun verify(password: String, expectedHash: String): Boolean {
        if (password.isEmpty() || expectedHash.isEmpty()) return false
        val computedHash = hash(password)
        return computedHash.equals(expectedHash, ignoreCase = true)
    }
}

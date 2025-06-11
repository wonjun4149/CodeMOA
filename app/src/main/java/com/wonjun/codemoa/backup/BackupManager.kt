package com.wonjun.codemoa.backup

import android.util.Base64
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.SecretKeyFactory
import javax.inject.Inject
import javax.inject.Singleton
import com.wonjun.codemoa.data.model.BackupData
import com.wonjun.codemoa.data.model.Card
import com.wonjun.codemoa.data.model.AppSettings

@Singleton
class BackupManager @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 백업 QR코드 생성
     */
    suspend fun createBackupQR(
        cards: List<Card>,
        settings: AppSettings,
        password: String
    ): Result<String> {
        return try {
            // 비밀번호 검증 (4자리 숫자)
            if (!isValidPassword(password)) {
                return Result.failure(IllegalArgumentException("비밀번호는 4자리 숫자여야 합니다."))
            }

            // 1. 솔트 생성
            val salt = generateRandomBytes(16)

            // 2. 비밀번호 해시 생성
            val passwordHash = hashPassword(password, salt)

            // 3. 백업 데이터 생성
            val backupData = BackupData(
                cards = cards,
                settings = settings,
                passwordHash = passwordHash
            )

            // 4. JSON 직렬화
            val jsonString = json.encodeToString(BackupData.serializer(), backupData)

            // 5. 데이터 압축
            val compressedData = compressData(jsonString.encodeToByteArray())

            // 6. 암호화를 위한 키 생성
            val key = deriveKeyFromPassword(password, salt)

            // 7. AES 암호화
            val iv = generateRandomBytes(16)
            val encryptedData = encryptAES(compressedData, key, iv)

            // 8. 최종 데이터 구조 생성
            val finalData = EncryptedBackupData(
                encryptedData = Base64.encodeToString(encryptedData, Base64.NO_WRAP),
                salt = Base64.encodeToString(salt, Base64.NO_WRAP),
                iv = Base64.encodeToString(iv, Base64.NO_WRAP),
                passwordHash = passwordHash
            )

            // 9. JSON으로 직렬화 후 Base64 인코딩
            val finalJson = json.encodeToString(EncryptedBackupData.serializer(), finalData)
            val finalEncoded = Base64.encodeToString(finalJson.encodeToByteArray(), Base64.NO_WRAP)

            // QR코드 크기 제한 확인 (일반적으로 2953자)
            if (finalEncoded.length > 2900) {
                return Result.failure(IllegalStateException("백업 데이터가 너무 큽니다. 일부 카드를 삭제 후 다시 시도해주세요."))
            }

            Result.success(finalEncoded)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * QR코드에서 백업 복원
     */
    suspend fun restoreFromQR(
        qrData: String,
        password: String
    ): Result<BackupData> {
        return try {
            // 비밀번호 검증
            if (!isValidPassword(password)) {
                return Result.failure(IllegalArgumentException("비밀번호는 4자리 숫자여야 합니다."))
            }

            // 1. Base64 디코딩
            val jsonBytes = Base64.decode(qrData, Base64.NO_WRAP)
            val jsonString = jsonBytes.decodeToString()

            // 2. JSON 파싱
            val encryptedBackup = json.decodeFromString(EncryptedBackupData.serializer(), jsonString)

            // 3. 비밀번호 검증
            val salt = Base64.decode(encryptedBackup.salt, Base64.NO_WRAP)
            val inputPasswordHash = hashPassword(password, salt)

            if (inputPasswordHash != encryptedBackup.passwordHash) {
                return Result.failure(IllegalArgumentException("잘못된 비밀번호입니다."))
            }

            // 4. 복호화 키 생성
            val key = deriveKeyFromPassword(password, salt)
            val iv = Base64.decode(encryptedBackup.iv, Base64.NO_WRAP)
            val encryptedData = Base64.decode(encryptedBackup.encryptedData, Base64.NO_WRAP)

            // 5. AES 복호화
            val decryptedData = decryptAES(encryptedData, key, iv)

            // 6. 압축 해제
            val decompressedData = decompressData(decryptedData)
            val jsonStringDecrypted = decompressedData.decodeToString()

            // 7. 백업 데이터 파싱
            val backupData = json.decodeFromString(BackupData.serializer(), jsonStringDecrypted)

            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * QR코드 크기를 여러 개로 분할 (선택적 기능)
     */
    suspend fun createMultiPartBackupQR(
        cards: List<Card>,
        settings: AppSettings,
        password: String,
        maxQRSize: Int = 2900
    ): Result<List<String>> {
        return try {
            val fullQR = createBackupQR(cards, settings, password).getOrThrow()

            if (fullQR.length <= maxQRSize) {
                Result.success(listOf(fullQR))
            } else {
                val parts = mutableListOf<String>()
                val chunkSize = maxQRSize - 20 // 헤더를 위한 여백
                val totalParts = (fullQR.length + chunkSize - 1) / chunkSize

                for (i in 0 until totalParts) {
                    val start = i * chunkSize
                    val end = minOf(start + chunkSize, fullQR.length)
                    val chunk = fullQR.substring(start, end)

                    // 멀티파트 헤더 추가 (CMOA = CodeMOA)
                    val partData = "CMOA${totalParts.toString().padStart(2, '0')}${(i + 1).toString().padStart(2, '0')}$chunk"
                    parts.add(partData)
                }

                Result.success(parts)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 분할된 QR코드들을 합쳐서 복원
     */
    suspend fun restoreFromMultiPartQR(
        qrParts: List<String>,
        password: String
    ): Result<BackupData> {
        return try {
            // 멀티파트 QR코드인지 확인
            if (qrParts.size == 1 && !qrParts.first().startsWith("CMOA")) {
                return restoreFromQR(qrParts.first(), password)
            }

            // 헤더 파싱 및 정렬
            val parts = mutableMapOf<Int, String>()
            var totalParts = 0

            for (qrData in qrParts) {
                if (!qrData.startsWith("CMOA")) {
                    return Result.failure(IllegalArgumentException("잘못된 QR코드 형식입니다."))
                }

                val header = qrData.substring(4, 8) // CMOA 이후 4자리
                totalParts = header.substring(0, 2).toInt()
                val partNumber = header.substring(2, 2).toInt()
                val data = qrData.substring(8)

                parts[partNumber] = data
            }

            // 모든 파트가 있는지 확인
            if (parts.size != totalParts) {
                return Result.failure(IllegalArgumentException("일부 QR코드가 누락되었습니다."))
            }

            // 파트들을 순서대로 합치기
            val fullData = StringBuilder()
            for (i in 1..totalParts) {
                fullData.append(parts[i] ?: return Result.failure(IllegalArgumentException("파트 $i 가 누락되었습니다.")))
            }

            // 복원 진행
            restoreFromQR(fullData.toString(), password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length == 4 && password.all { it.isDigit() }
    }

    private fun generateRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    private fun hashPassword(password: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update(password.toByteArray())
        return Base64.encodeToString(digest.digest(), Base64.NO_WRAP)
    }

    private fun deriveKeyFromPassword(password: String, salt: ByteArray): ByteArray {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
        val key = factory.generateSecret(spec)
        return key.encoded
    }

    private fun encryptAES(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(data)
    }

    private fun decryptAES(encryptedData: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(encryptedData)
    }

    private fun compressData(data: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val gzipStream = GZIPOutputStream(outputStream)
        gzipStream.write(data)
        gzipStream.close()
        return outputStream.toByteArray()
    }

    private fun decompressData(data: ByteArray): ByteArray {
        val inputStream = ByteArrayInputStream(data)
        val gzipStream = GZIPInputStream(inputStream)
        return gzipStream.readBytes()
    }
}

@kotlinx.serialization.Serializable
data class EncryptedBackupData(
    val version: String = "1.0",
    val createdAt: Long = System.currentTimeMillis(),
    val encryptedData: String, // AES 암호화된 데이터
    val salt: String, // 솔트 값
    val iv: String, // 초기화 벡터
    val passwordHash: String // SHA-256 해시
)
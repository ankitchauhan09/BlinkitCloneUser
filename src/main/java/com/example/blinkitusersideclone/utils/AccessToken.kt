package com.example.blinkitusersideclone.utils

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.charset.Charset

class AccessToken {

    private val request_scope = "https://www.googleapis.com/auth/firebase.messaging"

    @Throws(IOException::class)
    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        var jsonString = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"blinkitclone-948e2\",\n" +
                "  \"private_key_id\": \"b3d479326791e5bc5f004426c9e4b3cbab929058\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCuNUSTupAQbTmV\\npG4VSba7aI43m/y+4cBXQCmjkL2LMUkuEQJ08LMxjVHtplJjQbYTmxJc7Nu2B7oo\\nLPJIu7UvKoCxr/i0XQfHxC7gcM1xTthDyzaFTfZ/mh40btprAa76onJeD9rS7gCI\\nULx76eKQ7Is2lVqWm17l8wi1S3Id4DUyBeLBl9ev6W2Ey9aziszHzcFCaaUNbgtw\\n/uTDRr/bxjQKPT3Z/rY1r8yo43hoPgBCPaO7lNPMJQeXywGW81eNWjKLFuaK7oPX\\nVHvaVB1gYXUXCyQzhHJq2L7C6nP3HkuKoW/8idUsrmGc61cHfli2ulKyInJ6Rl9a\\nvYWvBkpnAgMBAAECggEATYbUKYA9q6nIFjHScbPX1+gsXlATdylFLS8XOLzptV+u\\nJwuX0c3qmTNAkifSA20TZ/87eVJJZuVicY2rFzY96Csckbbz0eSS2VKT3/sjWnkR\\non31mfZ7rAa8yW3J0xQxOEYB6p1Yr0UOxJtVfjm0Q/WY8KYf5eVoIqJ5LiFQvD02\\nOtg4dk21gNyM/4dcIygd2M5O85msWEzpZ5vs+Pl6iGQWwwIR+rqvat2wZmpDmg6m\\npmIXkLB/BCALqXLxSWE2w4kYn0mUdDY2EJ31Z1206XtPzWKcKh/PQy3C9oyKyrnh\\nDCtTh8FE5sIr5q0bp8MEUBrx+vPi2IPBIf3cKcK+0QKBgQDkBZTLnt2k2w4Myrka\\nIrWWUWF6bDG7OTZ3Lp2SaHWjfrhEzDMsfCecjI7MYkIUu+dL9ro8NhztC/SnoX49\\nslZRsYUM9Nn494RdenC38/kFNp1Gm8Bi0Zj1HOZ13RZ2AolZrpgDsRYSyfiqUNk8\\njgv3BIyuWuo3LlfE8In2d3PPUQKBgQDDlVZvUGvlb2/ZspQVrLramRe6yK2TgKcy\\nQ0XG9vJqvAe9nRDiOFLjUHmsYlOM5fTR+uutVKf7kMScbQlX3Hale+nTQUdjgNgr\\nPqEMHSKc+CiKey7dTlim2lQ9hzcnGzgGGHmwl3nPXgSCmlFOp0Jpdohvg6XqvfWn\\nlDTHNSjANwKBgBBk9lXNjlIUDeaNjNF8PWIZZ9DNhqXXbzsSdvHX4N1odwc7R1Kj\\nepWe5YV8axPijra/fpRlr567hVOoINN+xwYIpXBRCnsEENhHvWL8f6MIUjaJbEOu\\nnjjBYzCGPN8vP4DbNMLPMK8ZJm/YcEYSUHnqMdM3Zi+hfV4DjWfTHtiBAoGAdW1Q\\nO/0ZCuPhT998MZdHiL+qE6cxMAJqYC8r0c2qtpK2Qw3ueSQ//+LtaqaOa2YVm4z/\\nu028nbnJkfGVTn87bRf8x63F4uQU5cHwh4lmosAL9zkGZjEH4QXs1hQ89OnOtTo0\\nteX8aJiWNKPadVLO+OsF944fK9VpjHfbSE+Oa/kCgYAdTXu0k9zEotiNW6IaIHw0\\nFmLJFLHD1+XtOnkhQYSGmuZe4pfIIcThK+VFQ1sOLhdRJjPwJgKch59rmg/WLWQi\\nlSSOPu0L8SeCGM1N3Gj+iJqiGMzo6bTZYqpSFd3kasFnUZ7xuH7ZVFnApgjk1xnl\\nBmIPOvzy2CNIgkGUlDD3CQ==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-mauji@blinkitclone-948e2.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"103255738857733866483\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-mauji%40blinkitclone-948e2.iam.gserviceaccount.com\",\n" +
                "  \"universe_domain\": \"googleapis.com\"\n" +
                "}\n"

        val googleCredentials = GoogleCredentials
            .fromStream(jsonString.byteInputStream(Charset.defaultCharset()))
            .createScoped(request_scope)
        googleCredentials.refreshIfExpired()
        Log.d("AccessToken", "Retrieved token: $googleCredentials.accessToken.tokenValue")
        googleCredentials.accessToken.tokenValue
    }

}
package com.example.bitbloomadmin.notifications

import android.os.AsyncTask
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.collect.Lists
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class AccessToken {
    companion object {
        private const val FIREBASE_MESSAGING_SCOPE =
            "https://www.googleapis.com/auth/firebase.messaging"

        fun getAccessTokenAsync(callback: AccessTokenCallback) {
            AccessTokenTask(callback).execute()
        }
    }

    private class AccessTokenTask(private val callback: AccessTokenCallback) :
        AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val jsonString = "{\n" +
                        "  \"type\": \"service_account\",\n" +
                        "  \"project_id\": \"aitrustledger-3fe07\",\n" +
                        "  \"private_key_id\": \"893588cc375c86effd5794c5a82192ecf9d036e1\",\n" +
                        "  \"private_key\": \"REDACTED_KEY_START\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDQIIL2wdunHPOJ\\n9iXDQOs/B4mCXzOly2tkN0i/wcqJsC3aDgIh9Enyndwvg8CzCBDijd+h6daXEtAH\\ny2cX0J8Jgnat1KR7ezQxMv6kllwTz1jiAW/ljoCvFQjVnPhUkR5j3B6hNVHLG5jA\\nNEejiSxjvnd7NUyYz1YXz1beO9pTjb7AYyixfAwufk0p5opgMJOERUrv6bhR+vFD\\nBUVQpb9X2v63CFM83tGSbcm2ZZ6J7GVN/bnloOIqRo6Ua63d3AWe7EbeOFtjSYGn\\n9wTM6LA01i9c5HIWpaR/tIJ7NvBdnQdCMeWaNVYNrG3VzVoIZqT0IJOeKGeLG413\\ngyAP6CITAgMBAAECggEATsRvLDpUChv/46+/vPS9033jwe6L2mxxOV1e8AvEvnVp\\n9QOmNPmCXwr1gRoI/PjJgySUhW+9YjGhf8GwQ6gV+IBisAkxMvZ+2zvkeZ/aOzkW\\n93n5wlQ7SYfZbxbYOREvAjYVdYd7bDYGin9+uVq57QnxqDSUx6R4fcxfoP3f9Ayb\\nVOjZK248s1fBZ1fPxer/W5Cu8affz1XwAj+gKh0JH4WXlXhXLVNlgi9nrUtl4eVj\\n4RMzm1zDPgkeWFIw3U1EgAW8QCJICFS6aUQg7VAz07JqFQiHC0klghmsNg1yprYS\\nXU1fDzd5b61eYEOOWRYfdapGzEJvFogup+z8SLue8QKBgQDvkE9QNccAjdre8p44\\npA6DR55qrKKklZ48zuhU3Ntuiv3w2+r5pYvNpzn0SJuMcSkkRMo7+5v8THR5uBw0\\nLlbeMbYqu+GZ3WvUycLCesAXdS+6ZG4DxjOqr0lWbXpSRfGheHbII0PG694Ivuxt\\nrdqwQNTxxlzw4UVS+p2LaLOyewKBgQDeaAxZ/g+6L6qtwMfzoLYumLJziM3T3X8H\\ntBlIACNAidRSOUmKCvqtPpmH/bLrCauMWzFmYCyGjRK4VevSKlJtPLLcbAHPRmHC\\nkEB6P2dEL98fkTZIdSARYAxl/XlQsO8QQFNkbZOeMHbryVsUidBEYhxN2kzroAdd\\ny8lUOvSnSQKBgQDHn/YnfZiFYq+2xm+H/VVGmeBtPcwN38tLGpNJW/zSIao4Edm9\\nC/Dyft3xLKvJnOZOna/zydeSptcMMpn19YNXhg8pjwQQJg1b9ICBdK4rHfCWnmd6\\nZ6fYHFa9WV7rvfL6vZolbhVlZljP6uCzCINKCcwoaEY8R0twOPwdFjNfjwKBgQCb\\nV5Ng/ApiIk8Vg3Rln1gAXdkux1v0Yl4KtGmAF9CHkH2DXM4XpCmI/hNgn/vrHNr8\\ncumjNwOCi2CK7kCDj9n4wI+xU6ND8kXwDq9qc4SeZM10kAmZPG3ElRCz4AJvMNYZ\\nxMQ6firAL9uwPvjl9IhKaziXzCUz4peilOcvdOpTOQKBgHWIC0+CBiSfVJ/2V/t1\\nrZIeMFg1cRZm3A8zgn68khjlRRW2rutAdRVfgK+MOKf9f1J1FxnCl9PMu+owm8mO\\nyXVT8vUHMFR8kJi4+8dxvOuNH770ww+pluXtdpnAxwLpSpj4SKpmrImF2t4odO8L\\nn5MwjE9DAFEIARMoQs3iMWa5\\nREDACTED_KEY_END\\n\",\n" +
                        "  \"client_email\": \"firebase-adminsdk-fbsvc@aitrustledger-3fe07.iam.gserviceaccount.com\",\n" +
                        "  \"client_id\": \"111587825525969513663\",\n" +
                        "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                        "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                        "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                        "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40aitrustledger-3fe07.iam.gserviceaccount.com\",\n" +
                        "  \"universe_domain\": \"googleapis.com\"\n" +
                        "}\n"
                val stream: InputStream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))
                val googleCredentials = GoogleCredentials
                    .fromStream(stream)
                    .createScoped(Lists.newArrayList(FIREBASE_MESSAGING_SCOPE))
                googleCredentials.refreshIfExpired()
                googleCredentials.accessToken.tokenValue
            } catch (e: IOException) {
                Log.e("AccessToken", "Error retrieving access token", e)
                null
            }
        }

        override fun onPostExecute(token: String?) {
            callback.onAccessTokenReceived(token)
        }
    }

    interface AccessTokenCallback {
        fun onAccessTokenReceived(token: String?)
    }
}
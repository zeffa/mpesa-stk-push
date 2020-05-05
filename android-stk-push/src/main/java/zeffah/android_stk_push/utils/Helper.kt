package zeffah.android_stk_push.utils

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import zeffah.android_stk_push.network.response.ErrorType
import zeffah.android_stk_push.network.response.NetworkErrorListener
import zeffah.android_stk_push.utils.Constants.ERROR_KEY
import zeffah.android_stk_push.utils.Constants.MESSAGE_KEY
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*

fun generateLNMExpressPassword(
    businessShortCode: String,
    passKey: String,
    timestamp: String
): String {
    val password = "$businessShortCode$passKey$timestamp"
    return Base64.encodeToString(password.toByteArray(Charsets.ISO_8859_1), Base64.NO_WRAP)
}

fun getCurrentTimestamp(): String {
    return SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
}

suspend inline fun <T> safeApiCall(errorListener: NetworkErrorListener, crossinline responseFunc: suspend () -> T): T? {
    return try {
        withContext(Dispatchers.IO) {
            responseFunc.invoke() //responseFunc()
        }
    }catch (e: Exception) {
        withContext(Dispatchers.Main) {
            e.printStackTrace()
            Log.e("ApiCallException", "ApiError: ${e.localizedMessage}", e.cause)
            when(e) {
                is HttpException -> {
                    val errorBody = e.response()?.errorBody()
                    errorListener.onError(getErrorMessage(errorBody))
                }
                is SocketTimeoutException -> errorListener.onError(ErrorType.TIMEOUT)
                is IOException -> errorListener.onError(ErrorType.NETWORK)
                else -> errorListener.onError(ErrorType.UNKNOWN)
            }
        }
        null
    }
}

fun getErrorMessage(responseBody: ResponseBody?): String {
    return try {
        val jsonObject = JSONObject(responseBody!!.string())
        when {
            jsonObject.has(MESSAGE_KEY) -> jsonObject.getString(MESSAGE_KEY)
            jsonObject.has(ERROR_KEY) -> jsonObject.getString(ERROR_KEY)
            else -> "Something wrong happened"
        }
    } catch (e: Exception) {
        "Something wrong happened"
    }
}

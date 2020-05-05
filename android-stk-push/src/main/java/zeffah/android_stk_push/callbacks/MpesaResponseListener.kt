package zeffah.android_stk_push.callbacks

import zeffah.android_stk_push.network.response.ErrorType

interface MpesaResponseListener<T: Any> {
    fun onSuccess(response: T?)
    fun onFail(message: String?)
    fun onFail(errorType: ErrorType)
}
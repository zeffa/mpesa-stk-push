package zeffah.android_stk_push.network.response

interface NetworkErrorListener {
    fun onError(msg: String)
    fun onError(errorType: ErrorType)
}
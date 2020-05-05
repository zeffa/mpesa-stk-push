package zeffah.android_stk_push.network.response

enum class ErrorType {
    NETWORK, // IO
    TIMEOUT, // Socket
    UNKNOWN //Anything else
}
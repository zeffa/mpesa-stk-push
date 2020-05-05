package zeffah.android_stk_push.data

data class Mpesa(
    var BusinessShortCode: String,
    var Password: String,
    var Timestamp: String,
    var Amount: Float,
    var TransactionType: String,
    var PartyA: String,
    var PartyB: String,
    var PhoneNumber: String,
    var CallBackURL: String,
    var AccountReference: String,
    var TransactionDesc: String
)
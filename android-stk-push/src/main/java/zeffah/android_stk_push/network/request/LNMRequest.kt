package zeffah.android_stk_push.network.request

import zeffah.android_stk_push.data.TransactionType

class LNMRequest (
    var BusinessShortCode: String,
    var PassKey: String,
    var Type: TransactionType,
    var Amount: Float,
    var PartyA: String,
    var PartyB: String,
    var PhoneNumber: String,
    var CallBackURL: String,
    var AccountReference: String,
    var TransactionDesc: String
)
package zeffah.android_stk_push.data

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token")
    var token: String,
    @SerializedName("expires_in")
    var expiresInt: Int
)
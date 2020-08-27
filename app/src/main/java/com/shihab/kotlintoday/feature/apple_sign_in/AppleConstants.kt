package com.shihab.kotlintoday.feature.apple_sign_in

object AppleConstants {
    val CLIENT_ID = "net.sharetrip.android"
    val BEFORE_REDIRECT_URI = "https://xyz.net/api/v1/auth/apple-token"
    val REDIRECT_URI = "https://xyz.net?appleToken"
    val SCOPE = "name%20email"
    val RESPONSE_TYPE = "code%20id_token"
    val RESPONSE_MODE = "form_post"

    val AUTHURL = "https://appleid.apple.com/auth/authorize"
    val TOKENURL = "https://appleid.apple.com/auth/token"

//    https://appleid.apple.com/auth/
//    authorize?response_type=code%20id_token&response_mode=form_post&client_id=net.sharetrip.android
//    &scope=name%20email&state=fe6e75b4-3fb8-4946-8717-883b9c44b768&redirect_uri=https://stg-api.sharetrip.net/api/v1/auth/apple-token

}
package com.shihab.kotlintoday.feature.apple_sign_in

object AppleConstants {
    val CLIENT_ID = "net.sharetrip.android"
    val REDIRECT_URI = "https://sharetrip.net"
    val SCOPE = "name%20email"
    val RESPONSE_TYPE = "code%20id_token"
    val RESPONSE_MODE = "form_post"

    val AUTHURL = "https://appleid.apple.com/auth/authorize"
    val TOKENURL = "https://appleid.apple.com/auth/token"

    //https://appleid.apple.com/auth/authorize?client_id=net.sharetrip.android&
    // redirect_uri=https://sharetrip.net&response_type=code%20id_token&
    // scope=name%20email&response_mode=form_post&state
}
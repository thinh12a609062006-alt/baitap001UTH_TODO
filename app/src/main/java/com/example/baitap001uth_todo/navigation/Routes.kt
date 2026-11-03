package com.example.baitap001uth_todo.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding/{pageId}"   // {pageId} là chỗ trống để điền số trang
    const val HOME = "home"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val EMAIL = "email_login"
    const val PHONE = "phone_login"

    // Method tạo route thật từ số trang: onboarding(2) -> "onboarding/2"
    fun onboarding(pageId: Int): String = "onboarding/$pageId"

}
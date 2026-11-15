package com.example.baitap001uth_todo.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding/{pageId}"
    const val HOME = "home"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val EMAIL = "email_login"
    const val PHONE = "phone_login"
    const val LIST = "list"
    const val DETAIL = "detail/{taskId}"
    const val NOTIFICATIONS = "notifications"
    const val REMINDER_SETTINGS = "reminder_settings"
    fun detail(id: String) = "detail/$id"
    fun onboarding(pageId: Int): String = "onboarding/$pageId"

}
package com.example.baitap001uth_todo.data

import com.example.baitap001uth_todo.model.OnboardingPage

class OnboardingRepository {

    private val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            id = 1,
            title = "Mọi việc, gọn trong một chỗ",
            description = "Ghi lại bài tập, việc nhỏ, lịch học. Không còn quên việc nào.",
            buttonText = "Tiếp tục"
        ),
        OnboardingPage(
            id = 2,
            title = "Chia theo Học tập và Cuộc sống",
            description = "Mỗi việc một nhóm rõ ràng, để bạn biết nên làm gì trước.",
            buttonText = "Tiếp tục"
        ),
        OnboardingPage(
            id = 3,
            title = "Nhắc đúng lúc",
            description = "UTH Todo nhắc bạn trước hạn chót để không bỏ lỡ việc quan trọng.",
            buttonText = "Bắt đầu ngay"
        )
    )


    fun getAllPages(): List<OnboardingPage> {
        return pages
    }

    fun getPage(index: Int): OnboardingPage {
        return pages[index]
    }

    fun getTotalPages(): Int {
        return pages.size
    }
}
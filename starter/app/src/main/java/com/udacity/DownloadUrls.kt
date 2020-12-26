package com.udacity

import androidx.annotation.StringRes

enum class DownloadUrls(val url: String, @StringRes val title: Int) {
    GLIDE("https://github.com/bumptech/glide/archive/master.zip", R.string.glide_title),
    UDACITY("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip", R.string.udacity_title),
    RETROFIT("https://github.com/square/retrofit/archive/master.zip", R.string.retrofit_title)
}
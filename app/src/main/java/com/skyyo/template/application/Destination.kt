package com.skyyo.template.application

import androidx.annotation.StringRes
import com.skyyo.template.R

sealed class Destination(val route: String, @StringRes val resourceId: Int = 0) {
    object SignIn : Destination("signIn")

    object Tab1 : Destination("tab1", R.string.app_name)
    object Tab2 : Destination("tab2", R.string.app_name)
    object Tab3 : Destination("tab3", R.string.app_name)

    object Profile : Destination("profile", R.string.app_name)
}

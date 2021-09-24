package com.skyyo.template.application.models.local

import com.skyyo.template.R

sealed class PagingException(open val stringRes: Int) : Throwable() {
    object NetworkError : PagingException(R.string.network_error)
    class Error(stringRes: Int) : PagingException(stringRes)
}

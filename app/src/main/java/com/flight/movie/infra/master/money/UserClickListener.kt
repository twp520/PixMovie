package com.flight.movie.infra.master.money

import android.view.View

/**
 * create by colin
 * 2024/7/25
 */
abstract class UserClickListener(private val listener: (view: View) -> Unit) : View.OnClickListener {

    override fun onClick(v: View) {
        ShareHelper.userActivityClicked()
        listener.invoke(v)
    }
}
package com.shihab.kotlintoday.utility

// todo
// workmanager : working now : DONE
// mvvm swipe delete animation, edit notes
// Dagger HILT with MVVB : Done
// Paging 3.0 : DONE
// type script
// CICL
// testing..Unit test
// mockito.
// Flow
// Extend LiveData : https://developer.android.com/topic/libraries/architecture/livedata#extend_livedata

// Recycler
//https://medium.com/better-programming/android-recyclerview-with-beautiful-animations-5e9b34dbb0fa

// Dialog Fragment
// https://www.youtube.com/watch?v=--dJm6z5b0s

// stetho : Done
// retrofit : Done

/*fun showPriceCheckDialog(oldPrice: Int, newPrice: Int?) {
    val ft = fragmentManager?.beginTransaction()!!
    val prev = fragmentManager?.findFragmentByTag("dialog")
    if (prev != null) {
        ft.remove(prev)
    }
    ft.addToBackStack(null)
    val dialog = AlertDialogWithCustomView.newInstance(oldPrice, newPrice)
    dialog.setTargetFragment(this, 0)
    dialog.show(ft, "dialog")
}*/

/* interface DialogButtonClicked {
    fun onContinueClicked()
    fun onAnotherFlightClicked()
}*/
/*
package net.sharetrip.ui.flights.checkout.view.summary

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.dialog_fragment_price_update.*
import net.sharetrip.R
import java.text.NumberFormat
import java.util.*

class AlertDialogWithCustomView : DialogFragment() {

    var value = 0
    val continue_payment = 1
    val another_flight = 2

    var dialogButtonClicked: DialogButtonClicked? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_price_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_continue.setOnClickListener {
            value = continue_payment
            dialog?.cancel()
        }

        tv_another_flight.setOnClickListener {
            value = another_flight
            dialog?.cancel()
        }

        tvOldTicketPrice.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(previousFare)
        tv_update_price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(updatedFare)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment: Fragment? = targetFragment
        if (fragment is DialogButtonClicked) {
            setDialogButtonClickedListener(fragment)
        } else {
            throw RuntimeException("you must implement OnClickListener")
        }
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
    }

    fun setDialogButtonClickedListener(callback: DialogButtonClicked) {
        this.dialogButtonClicked = callback
    }

    companion object {
        var previousFare: Int? = null
        var updatedFare: Int? = null
        fun newInstance(old: Int, new: Int?): AlertDialogWithCustomView {
            val f = AlertDialogWithCustomView()
            previousFare = old!!
            updatedFare = new!!
            return f
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (value == continue_payment) {
            dialogButtonClicked?.onContinueClicked()
        } else if (value == another_flight) {
            dialogButtonClicked?.onAnotherFlightClicked()
        }
    }
}
* */
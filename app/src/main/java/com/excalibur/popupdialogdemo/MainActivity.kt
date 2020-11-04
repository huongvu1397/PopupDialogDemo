package com.excalibur.popupdialogdemo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.excalibur.popupdialogdemo.databinding.DialogTestBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var point = IntArray(2)
        btnShowDialog.screenLocationSafe { dY ->
            point = dY
        }

        btnShowDialog.setOnClickListener {
            val dialog = AppBaseDialog<DialogTestBinding>(this, R.layout.dialog_test, -1)
            dialog.configurePosition(0, point[1] + 50,Gravity.TOP)
            dialog.show()
        }
    }
}

fun View.screenLocationSafe(callback: (IntArray) -> Unit) {
    post {
        val point = IntArray(2)
        getLocationOnScreen(point)
        val (x, y) = point
        callback(point)
    }
}
package com.anwesh.uiprojects.linkedpumpcolorrectview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.pumpcolorrectview.PumpColorRectView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PumpColorRectView.create(this)
    }
}

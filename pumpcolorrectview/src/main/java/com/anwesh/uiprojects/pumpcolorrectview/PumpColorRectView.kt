package com.anwesh.uiprojects.pumpcolorrectview

/**
 * Created by anweshmishra on 14/09/19.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val scGap : Float = 0.01f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.3f
val lWFactor : Float = 3f
val lHFactor : Float = 6.9f
val foreColor : Int = Color.parseColor("#f44336")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 25

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawPumpRect(sc1 : Float, sc2 : Float, size : Float, paint : Paint) {
    val lw : Float = size / lWFactor
    val lh : Float = size / lHFactor
    val ly : Float = (size - lh) * (sc2 - sc1)
    save()
    translate(0f, ly)
    drawRect(RectF(-lw, -lh, lw, 0f), paint)
    drawLine(0f, 0f, 0f, -ly, paint)
    restore()
    drawRect(RectF(-size, 0f, size, size * sc2), paint)
}

fun Canvas.drawPCRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(gap * (i + 1), w / 2)
    drawPumpRect(scale.divideScale(0, 2), scale.divideScale(1, 2), size, paint)
    restore()
}

class PumpColorRectView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class PCRNode(var i : Int, val state : State = State()) {

        private var next : PCRNode? = null
        private var prev : PCRNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = PCRNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas?.drawPCRNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : PCRNode {
            var curr : PCRNode? = prev
            if (dir == 1) {
                curr = prev
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}
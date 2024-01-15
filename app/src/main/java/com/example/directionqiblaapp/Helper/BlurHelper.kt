package com.example.directionqiblaapp.Helper

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object BlurHelper {
    fun blur(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
        val inputBitmap = Bitmap.createBitmap(bitmap)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val rs = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        val allocationIn = Allocation.createFromBitmap(rs, inputBitmap)
        val allocationOut = Allocation.createFromBitmap(rs, outputBitmap)

        blurScript.setInput(allocationIn)
        blurScript.setRadius(radius)
        blurScript.forEach(allocationOut)

        allocationOut.copyTo(outputBitmap)

        rs.destroy()

        return outputBitmap
    }
}
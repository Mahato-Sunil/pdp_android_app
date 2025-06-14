/*
This class is responsible to convert the captured image to base64 format
steps :
    1   =>  get the bitmap image from the file path using BitmapFactory.decodeFile(filepath)
    2   =>  create the byte output stream using ByteArrayOutputStream()
    3   =>  compress the bitmap to jpg or png format using bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    4   => create the byre array using stream.toByteArray()
    5   =>  encode the byte array to base64 using Base64.encodeToString(byteArray, Base64.DEFAULT)
    6   =>  return the base64 string


  ## Converting the base64 to image
  Steps
  1     =>  get the base64 formatted string
  2     => create the byte array using Base64.decode(base64String, Base64.DEFAULT)
  3     =>  create the bitmap using BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
  4     =>  return the bitmap
 */

package com.officialsunil.pdpapplication.utils.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

object ImageToBase64 {

    // function to convert the Image to Base64 format
    fun convertImageToBase64Format(imagePath: String): String? {
        val bitmap = BitmapFactory.decodeFile(imagePath) ?: return null
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // logging the sizes of the image
        val bitmapSize = (byteArray.size) / (1024 * 1024)
        val base64Size = (base64String.toByteArray(Charsets.UTF_8).size) / (1024 * 1024)

        Log.d("ImageToBase64", "Bitmap Size: $bitmapSize \nBase64 Size: $base64Size")
        return base64String
    }

    // function to convert the base 64 to the image bitmap
    fun convertBase64ToImage(base64String: String): Bitmap? {
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
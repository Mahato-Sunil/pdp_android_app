package com.officialsunil.pdpapplication.tfLiteModule

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.core.Core

object LeafBackgroundRemover {

    /** Convert Bitmap → Mat */
    private fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        return mat
    }

    /** Convert Mat → Bitmap */
    private fun matToBitmap(mat: Mat): Bitmap {
        val bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, bmp)
        return bmp
    }

    /** Extract leaf mask using HSV color space and contour refinement */
    private fun extractLeafMask(original: Mat): Mat {
        // Convert to HSV for color-based segmentation
        val hsv = Mat()
        Imgproc.cvtColor(original, hsv, Imgproc.COLOR_RGB2HSV)

        // Define HSV range for leaf colors (adjust these ranges based on your dataset)
        // Green leaves: H: 30-90, S: 40-255, V: 20-255
        // Yellow/brown infected areas: H: 0-30 or 90-120, S: 40-255, V: 20-255
        val lowerGreen = Scalar(30.0, 40.0, 20.0) // H, S, V
        val upperGreen = Scalar(90.0, 255.0, 255.0)
        val lowerInfected = Scalar(0.0, 40.0, 20.0)
        val upperInfected = Scalar(30.0, 255.0, 255.0)

        // Create masks for green and infected areas
        val maskGreen = Mat()
        Core.inRange(hsv, lowerGreen, upperGreen, maskGreen)
        val maskInfected = Mat()
        Core.inRange(hsv, lowerInfected, upperInfected, maskInfected)

        // Combine masks
        val combinedMask = Mat()
        Core.bitwise_or(maskGreen, maskInfected, combinedMask)

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(combinedMask, combinedMask, Size(5.0, 5.0), 0.0)

        // Morphological operations to refine mask
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(5.0, 5.0))
        Imgproc.morphologyEx(combinedMask, combinedMask, Imgproc.MORPH_CLOSE, kernel) // Fill holes
        Imgproc.morphologyEx(combinedMask, combinedMask, Imgproc.MORPH_OPEN, kernel) // Remove noise

        // Find contours
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            combinedMask,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        if (contours.isEmpty()) {
            return Mat.zeros(original.size(), CvType.CV_8UC1)
        }

        // Select largest contour (assumed to be the leaf)
        val leafContour = contours.maxByOrNull { Imgproc.contourArea(it) }

        // Create final mask
        val mask = Mat.zeros(original.size(), CvType.CV_8UC1)
        if (leafContour != null) {
            // Validate contour size (e.g., 10-80% of image area)
            val contourArea = Imgproc.contourArea(leafContour)
            val imageArea = original.size().area()
            if (contourArea > 0.1 * imageArea && contourArea < 0.8 * imageArea) {
                Imgproc.drawContours(mask, listOf(leafContour), -1, Scalar(255.0), -1)
            } else {
                // Fallback: Return empty mask or adjust parameters
                return Mat.zeros(original.size(), CvType.CV_8UC1)
            }
        }

        // Smooth mask edges
        Imgproc.GaussianBlur(mask, mask, Size(3.0, 3.0), 0.0)

        return mask
    }

    /** Apply mask to keep leaf and make background transparent */
    private fun applyMask(original: Mat, mask: Mat): Mat {
        // Ensure original has alpha channel (RGBA)
        val rgba = Mat()
        if (original.channels() == 3) {
            Imgproc.cvtColor(original, rgba, Imgproc.COLOR_RGB2RGBA)
        } else {
            original.copyTo(rgba)
        }

        // Create transparent background
        val result = Mat.zeros(rgba.size(), CvType.CV_8UC4)
        Core.bitwise_and(rgba, rgba, result, mask)

        return result
    }

    /** Optional: Use GrabCut for complex backgrounds */
    private fun refineWithGrabCut(original: Mat, mask: Mat): Mat {
        val bgdModel = Mat()
        val fgdModel = Mat()
        val rect = Rect(10, 10, original.cols() - 20, original.rows() - 20) // ROI around leaf

        val grabCutMask = Mat()
        Imgproc.grabCut(
            original,
            grabCutMask,
            rect,
            bgdModel,
            fgdModel,
            5,
            Imgproc.GC_INIT_WITH_RECT
        )

        // Refine mask: Keep probable/foreground pixels
        val refinedMask = Mat.zeros(mask.size(), CvType.CV_8UC1)
        Core.compare(grabCutMask, Scalar(Imgproc.GC_FGD.toDouble()), refinedMask, Core.CMP_EQ)
        Core.compare(grabCutMask, Scalar(Imgproc.GC_PR_FGD.toDouble()), refinedMask, Core.CMP_EQ)

        return refinedMask
    }

    /** Main function: Background removal pipeline */
    fun removeBackground(original: Bitmap): Bitmap {
        val src = bitmapToMat(original)
        val mask = extractLeafMask(src)

        // Optional: Uncomment to use GrabCut for complex cases
//         val refinedMask = refineWithGrabCut(src, mask)
//         val resultMat = applyMask(src, refinedMask)

        val resultMat = applyMask(src, mask)
        return matToBitmap(resultMat)
    }
}
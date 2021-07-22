package com.reedy.imagelabeler.utils

import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.ImageData
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.lang.StringBuilder


object AnnotationGenerators {

    private const val TAG = "AnnotationGenerator"

    fun getPascalVocAnnotation(box: Box, annotation: ImageData): String {
        return "<annotation>\n" +
                "\t<filename>${annotation.fileName}</filename>\n" +
                "\t<path>${annotation.path}</path>\n" +
                "\t<source>\n" +
                "\t\t<database>${annotation.source}</database>\n" +
                "\t</source>\n" +
                "\t<size>\n" +
                "\t\t<width>${annotation.imageWidth}</width>\n" +
                "\t\t<height>${annotation.imageHeight}</height>\n" +
                "\t\t<depth>${annotation.depth}</depth>\n" +
                "\t</size>\n" +
                "\t<segmented>${annotation.segmented}</segmented>\n" +
                "\t<object>\n" +
                "\t\t<name>${box.label}</name>\n" +
                "\t\t<pose>${annotation.pose}</pose>\n" +
                "\t\t<truncated>${annotation.truncated}</truncated>\n" +
                "\t\t<difficult>${annotation.difficult}</difficult>\n" +
                "\t\t<bndbox>\n" +
                "\t\t\t<xmin>${box.xMin}</xmin>\n" +
                "\t\t\t<xmax>${box.xMax}</xmax>\n" +
                "\t\t\t<ymin>${box.yMin}</ymin>\n" +
                "\t\t\t<ymax>${box.yMax}</ymax>\n" +
                "\t\t</bndbox>\n" +
                "\t</object>\n" +
                "</annotation>"
    }

    fun writeTensorflowObjectDetectionCsv(data: List<ImageData>): String {
        val builder = StringBuilder()
        val header = "filename, class, width, height, xmin, ymin, xmax, ymax"

        builder.append(header)
        builder.append("\n")
        data.forEach { image ->
            image.boxes.forEach { box ->
                builder.append(image.fileName)
                builder.append(",")
                builder.append(box.label)
                builder.append(",")
                builder.append(box.width)
                builder.append(",")
                builder.append(box.height)
                builder.append(",")
                builder.append(box.xMin.toInt())
                builder.append(",")
                builder.append(box.yMin.toInt())
                builder.append(",")
                builder.append(box.xMax.toInt())
                builder.append(",")
                builder.append(box.yMax.toInt())
                builder.append("\n")
            }
        }

        return builder.toString()
    }

}
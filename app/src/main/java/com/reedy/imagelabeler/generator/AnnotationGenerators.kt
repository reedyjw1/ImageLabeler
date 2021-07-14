package com.reedy.imagelabeler.generator

import com.reedy.imagelabeler.model.Annotation
import com.reedy.imagelabeler.model.Box

object AnnotationGenerators {

    fun getPascalVocAnnotation(box: Box, annotation: Annotation): String {
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
                "\t\t\t<xmin>${box.relativeToBitmapXMin}</xmin>\n" +
                "\t\t\t<xmax>${box.relativeToBitmapXMax}</xmax>\n" +
                "\t\t\t<ymin>${box.relativeToBitmapYMin}</ymin>\n" +
                "\t\t\t<ymax>${box.relativeToBitmapYMax}</ymax>\n" +
                "\t\t</bndbox>\n" +
                "\t</object>\n" +
                "</annotation>"
    }

}
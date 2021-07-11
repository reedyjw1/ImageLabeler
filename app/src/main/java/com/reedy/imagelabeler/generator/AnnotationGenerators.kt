package com.reedy.imagelabeler.generator

import com.reedy.imagelabeler.model.Box

object AnnotationGenerators {

    fun getPascalVocAnnotation(box: Box): String {
        return "<annotation>\n" +
                "\t<filename>${box.fileName}</filename>\n" +
                "\t<path>${box.path}</path>\n" +
                "\t<source>\n" +
                "\t\t<database>${box.source}</database>\n" +
                "\t</source>\n" +
                "\t<size>\n" +
                "\t\t<width>${box.imageWidth}</width>\n" +
                "\t\t<height>${box.imageHeight}</height>\n" +
                "\t\t<depth>${box.depth}</depth>\n" +
                "\t</size>\n" +
                "\t<segmented>${box.segmented}</segmented>\n" +
                "\t<object>\n" +
                "\t\t<name>${box.labelAsInt}</name>\n" +
                "\t\t<pose>${box.pose}</pose>\n" +
                "\t\t<truncated>${box.truncated}</truncated>\n" +
                "\t\t<difficult>${box.difficult}</difficult>\n" +
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
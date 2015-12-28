package common

import org.apache.commons.io.IOUtils

object Utils {
    fun readLinesFromClasspathFile(file: String):  List<String>  {
        return IOUtils.readLines(this.javaClass.classLoader.getResourceAsStream(file))
    }
}
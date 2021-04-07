package q.rorbin.component.gradle.plugin.common

import java.io.File
import java.io.FileFilter

/**
 * @author changhai.qiu
 */

enum class FileType {
    FILE, DIRECTORY, ALL
}

/**
 * recursion travere all files
 */
fun File.traverse(
        filter: FileFilter = FileFilter { true },
        fileType: FileType = FileType.ALL,
        callback: (File) -> Unit
) {
    if (this.isFile && (fileType == FileType.ALL || fileType == FileType.FILE)
        && filter.accept(this)
    ) {
        callback(this)
        return
    }
    if (this.isDirectory && (fileType == FileType.ALL || fileType == FileType.DIRECTORY)
        && filter.accept(this)
    ) {
        callback(this)
    }
    if (this.isDirectory) {
        this.listFiles()?.forEach {
            it.traverse(filter, fileType, callback)
        }
    }
}
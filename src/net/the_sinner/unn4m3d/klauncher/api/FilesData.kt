package net.the_sinner.unn4m3d.klauncher.api

import net.the_sinner.unn4m3d.filecheck.FileInfo

/**
 * Created by unn4m3d on 15.12.16.
 */
class FilesData(
        val dir : String,
        val fileinfo : List<FileInfo>,
        val ignore : Regex
)
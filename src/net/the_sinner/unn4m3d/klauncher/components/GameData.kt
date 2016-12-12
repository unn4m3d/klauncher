package net.the_sinner.unn4m3d.klauncher.components

import net.the_sinner.unn4m3d.filecheck.FileInfo

/**
 * Created by unn4m3d on 11.12.16.
 */
data class GameData(
        val username : String,
        val session : String,
        val files : Map<String, FileInfo>
)
package net.the_sinner.unn4m3d.klauncher.components

import javafx.collections.FXCollections

/**
 * Created by unn4m3d on 15.12.16.
 */

fun convertStackTrace(e : Exception) = FXCollections.observableArrayList<String>(e.stackTrace.map{
    "${it.className}.${it.methodName} (${it.fileName} : ${it.lineNumber})"
})
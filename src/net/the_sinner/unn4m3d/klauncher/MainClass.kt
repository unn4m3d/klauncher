package net.the_sinner.unn4m3d.klauncher

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import net.the_sinner.unn4m3d.filecheck.FileInfo
import net.the_sinner.unn4m3d.klauncher.components.Game
import net.the_sinner.unn4m3d.klauncher.components.GameData
import net.the_sinner.unn4m3d.klauncher.components.Settings
import java.io.File
import java.util.*
import khttp.get
import net.the_sinner.unn4m3d.klauncher.gui.MainForm

//import net.the_sinner.unn4m3d.klauncher.gui.MainForm

fun main(args : Array<String>) {
    /*val gameDir = File("/home/unn4m3d/.tstest/thesinner")
    val settings = Settings(640, 480, "test key", "Minecraft unn4m3d", false, File("/home/unn4m3d/.tstest/").absolutePath, "1.7.10")
    val data = GameData("unn4m3d", "0123456789ABCDEF", "aaaa-aaaa-aa-aaa")

    val game = Game(data)
    try {
        game.launch(gameDir.absolutePath, settings) {
            System.out.println(it)
        }
    } catch(e : Exception) {
        e.printStackTrace()
    }*/
    val form = MainForm()
}

package net.the_sinner.unn4m3d.klauncher

import net.minecraft.launcher.Launcher
import net.the_sinner.unn4m3d.klauncher.api.API
import net.the_sinner.unn4m3d.klauncher.components.LauncherConfig
import net.the_sinner.unn4m3d.klauncher.components.getAppData
import net.the_sinner.unn4m3d.klauncher.gui.MainApp

//import net.the_sinner.unn4m3d.klauncher.gui.MainForm

//import net.the_sinner.unn4m3d.klauncher.gui.MainForm


fun main(args : Array<String>) {
    if(!config.file.exists())
        config.save()
    config.load()
    MainApp.main(args)
}

val config = LauncherConfig(getAppData().resolve(Config.APP_FOLDER).resolve("launcher.json"))
var forceUpdate = false

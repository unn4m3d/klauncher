package net.the_sinner.unn4m3d.klauncher

import net.the_sinner.unn4m3d.klauncher.components.getAppData
import net.the_sinner.unn4m3d.klauncher.components.getPlatform
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JOptionPane
import kotlin.system.exitProcess

/**
 * Created by unn4m3d on 17.12.16.
 */

fun old_main(args : Array<String>)
{
    if(!config.file.exists())
        config.save()
    config.load()
    config.setDefault("memory",1024)
    val memory = config.getOpt<Int>("memory",1024)

    val path = Dummy.getPath()
    val cname = Dummy.getCname() // Gets canonical name of MainClassKt
    val params = arrayListOf(
            "java",
            "-Xms${memory}M",
            "-Xmx${memory}M",
            "-XX:MaxPermSize=128m",
            "-Dfile.encoding=UTF-8")

    if(getPlatform().startsWith("osx"))
    {
        params.add("-Xdock:name=Minecraft")
        // TODO icon
    }
    config.save()

    params.add("-classpath")
    params.add(path)
    params.add(cname)

    try {
        var pb = ProcessBuilder(params)
        val folder = getAppData().resolve(Config.APP_FOLDER)
        if (!folder.exists())
            folder.mkdirs()
        pb.directory(folder)
        pb = pb.inheritIO()
        println(params)

        val sdf = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
        val log = folder.resolve("launcher-${sdf.format(Date())}.log")
        pb.redirectOutput(log)
        pb.redirectError(log)
        val proc = pb.start()
    } catch(e : Exception) {
        e.printStackTrace()
        JOptionPane.showMessageDialog(null, "Exception", "${e.javaClass.name}: ${e.message}", JOptionPane.ERROR_MESSAGE)
    }
}

fun main(args : Array<String>)
{
    old_main(args)
    exitProcess(0)
}

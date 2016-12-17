package net.the_sinner.unn4m3d.klauncher

import net.the_sinner.unn4m3d.klauncher.components.getPlatform
import javax.swing.JOptionPane
import kotlin.system.exitProcess

/**
 * Created by unn4m3d on 17.12.16.
 */

fun old_main(args : Array<String>)
{
    if(config.file.exists())
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

    var pb = ProcessBuilder(params)
    pb = pb.inheritIO()

    val proc = pb.start()
}

fun main(args : Array<String>)
{
    try{
        old_main(args)
        exitProcess(0)
    } catch (e : Exception) {
        JOptionPane.showMessageDialog(null,e.message,e.javaClass.name,0)
        e.printStackTrace()
    }
}

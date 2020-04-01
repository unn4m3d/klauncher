package net.the_sinner.unn4m3d.klauncher.components

import net.launcher.utils.java.eURLClassLoader
import net.minecraft.launcher.Launcher
import net.the_sinner.unn4m3d.klauncher.Config
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.io.File
import java.net.URL
import java.util.*
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.system.exitProcess

/**
 * Created by unn4m3d on 11.12.16.
 */


class Game(val data : GameData) : JFrame(){

    var mcApplet : Launcher? = null
    @Throws(Exception::class)
    fun launch(gameDir : String, sett : Settings, callback : (String) -> Unit)
    {
        System.setProperty("user.dir", gameDir)
        val bin = "$gameDir${File.separator}bin${File.separator}"
        callback("Launching in $gameDir")
        callback("Searching for JARs")

        (File(gameDir).parentFile.resolve("asm/ccl_modular")).mkdirs()

        val fw = File(gameDir).walkTopDown()
        var list = ArrayList<URL>()

        fw.iterator().forEach {
            if (!it.isDirectory && it.name.endsWith(".jar") && !it.absolutePath.replace(gameDir,"").contains("mods")) {
                list.add(it.toURI().toURL())
                callback("Adding ${it.absolutePath}")
            }
        }
        callback("Found ${list.size} JARs")

        val cl = eURLClassLoader(list.toTypedArray())
        callback("${cl.urLs.size} JARs in class loader")
        var old = false

        callback("Determining launching algorithm")
        try{
            cl.loadClass("net.minecraft.Minecraft")
            old = true
            callback("Detected old version")
        } catch (e : Exception) {
            callback("Detected new version")
        }

        callback("Continuing...")

        val username = data.username
        val sessionId = data.sessionId
        callback("Decrypted sid")
        //println("SID ${sessionId}, ATK $atok")

        if(old) {

            callback("Preparing old minecraft")
            addWindowListener(CloseWindowListener{
                if(mcApplet != null)
                {
                    mcApplet!!.stop()
                    mcApplet!!.destroy()
                    exitProcess(0)
                }
            })

            background = Color.BLACK
            foreground = Color.BLACK

            mcApplet = Launcher(bin,list.toTypedArray())
            mcApplet!!.customParameters["username"] = username
            mcApplet!!.customParameters["sessionid"] = sessionId
            mcApplet!!.customParameters["stand-alone"] = "true"
            // TODO : Autoenter

            setSize(sett.width,sett.height + 28)
            minimumSize = Dimension(sett.width,sett.height+28)
            title = sett.title

            mcApplet!!.background = Color.BLACK
            mcApplet!!.foreground = Color.BLACK
            layout = BorderLayout()
            add(mcApplet!!,BorderLayout.CENTER)

            validate()

            if(sett.fullscreen)
                extendedState = JFrame.MAXIMIZED_BOTH

            isVisible = true
            callback("Initializing")
            mcApplet!!.init()
            callback("Running Minecraft")
            mcApplet!!.start()
        } else {
            callback("Preparing new minecraft")

            val jarpath = File(gameDir).resolve("versions").resolve(sett.version).absolutePath

            val libpath = File(jarpath).resolve("natives").absolutePath
            System.setProperty("fml.ignoreInvalidMinecraftCertificates", "true")
            System.setProperty("fml.ignorePatchDiscrepancies", "true")
            System.setProperty("org.lwjgl.librarypath", libpath)
            System.setProperty("net.java.games.input.librarypath", libpath)
            System.setProperty("java.library.path", libpath)
            callback("Libdir : $libpath")
            //System.setProperty("user.dir", gameDir)

            var params = ArrayList<String>()
            if (sett.fullscreen) {
                params.add("--fullscreen")
                params.add("true")
            } else {
                params.add("--width")
                params.add(sett.width.toString())
                params.add("--height")
                params.add(sett.height.toString())
            }

            // TODO : Autoenter

            try {
                callback("Detecting authlib agent")
                cl.loadClass("com.mojang.authlib.Agent")
                callback("Authlib agent is present")

                params.add("--accessToken")
                params.add(sessionId)
                params.add("--uuid")

                params.add(data.accessToken)

                params.add("--userProperties")
                params.add("{}")

                params.add("--assetIndex")
                params.add(sett.version)

            } catch (e: ClassNotFoundException) {
                callback("Cannot detect mojang authlib agent")
                params.add("--session")
                params.add(sessionId)
            }

            callback("Continuing...")
            params.add("--username")
            params.add(username)
            params.add("--version")
            params.add(sett.version)
            params.add("--gameDir")
            params.add(gameDir)
            params.add("--assetsDir")

            if (sett.version.replace(Regex("[^0-9]"), "").toInt() < 173) {
                params.add(File(sett.assets).resolve("assets/virtual/legacy").absolutePath)
            } else {
                params.add(File(sett.assets).resolve("assets").absolutePath)
            }

            var tweaked = false

            try {
                callback("Looking for LiteLoader...")
                cl.loadClass("com.mumfrey.liteloader.launch.LiteLoaderTweaker")
                params.add("--tweakClass")
                params.add("com.mumfrey.liteloader.launch.LiteLoaderTweaker")
                tweaked = true
                callback("Detected LiteLoader")
            } catch (e: ClassNotFoundException) {
            }

            try {
                callback("Looking for FML Tweaker...")
                cl.loadClass("cpw.mods.fml.common.launcher.FMLTweaker")
                params.add("--tweakClass")
                params.add("cpw.mods.fml.common.launcher.FMLTweaker")
                tweaked = true
                callback("Detected FML")
            } catch (e: ClassNotFoundException) {
                callback("FML Tweaker not found")
            }

            try {
                callback("Looking for new FML Tweaker...")
                cl.loadClass("net.minecraftforge.fml.common.launcher.FMLTweaker")
                params.add("--tweakClass")
                params.add("net.minecraftforge.fml.common.launcher.FMLTweaker")
                params.add("--versionType")
                params.add("Forge")
                callback("Detected new FML")
                params.add("--userType")
                params.add("legacy")
                tweaked = true
            } catch(e: ClassNotFoundException) {
                callback("New FML Tweaker not found")
            }

            callback("Loading...")
            val start = cl.loadClass(if (tweaked) {
                "net.minecraft.launchwrapper.Launch"
            } else {
                "net.minecraft.client.main.Main"
            })

            val main = start.getMethod("main", Array<String>(0) { "" }.javaClass)
            callback("!!! LAUNCHING !!!")
            println("PARAMS : ${params.joinToString()}")
            val hook = ShutdownHook.getShutdownHook()
            try {

                Runtime.getRuntime().addShutdownHook(hook)
                main.invoke(null, params.toTypedArray());
            } catch (e: Exception) {
                ShutdownHook.crashed = true;
                e.printStackTrace()
                JOptionPane.showMessageDialog(null, "Minecraft crashed. See launcher and client log for details", "Minecraft crashed", JOptionPane.ERROR_MESSAGE)

            } finally {
                Runtime.getRuntime().removeShutdownHook(hook)
            }
        }

    }
}
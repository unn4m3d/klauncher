package net.the_sinner.unn4m3d.klauncher.components

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.management.ManagementFactory
import java.net.URLEncoder
import java.util.zip.ZipInputStream

/**
 * Created by unn4m3d on 16.12.16.
 */

val os = System.getProperty("os.name")
val arch = System.getProperty("os.arch")

fun getPlatform() : String
{
    val bits = if(arch.contains("64")){"64"}else{"32"}
    if(os.toUpperCase().contains("WIN"))
    {
        return "win$bits"
    }
    else if(os.toUpperCase().contains("LINUX"))
    {
        return "linux$bits"
    }
    else if(os.toUpperCase().contains("MAC") || os.toUpperCase().contains("OS X"))
    {
        return "osx$bits"
    }
    else
    {
        return "generic"
    }
}

fun getAppData() : File
{
    if(getPlatform().startsWith("osx"))
    {
        return File(System.getProperty("user.home")).resolve("/Library/Application Support")
    }
    else if(getPlatform().startsWith("win"))
    {
        return File(System.getenv("APPDATA"))
    }
    else
    {
        return File(System.getProperty("user.home"))
    }
}

fun fileJoin(file : File, path : String) = file.resolve(path)

fun Byte.toHex() : String
{
    val hex = "0123456789ABCDEF"
    val high = (this.toInt() and 0xF)
    val low = (this.toInt() shr 4) and 0xF
    return "${hex[high]}${hex[low]}"
}

fun encodeRPath(path : String) = path.split("/").map{
    URLEncoder.encode(it, "UTF-8").replace("+", "%20")
}.joinToString("/")

fun ramSize() : Long
{
    try {
        val bean = ManagementFactory.getOperatingSystemMXBean() as com.sun.management.OperatingSystemMXBean
        return bean.totalPhysicalMemorySize
    } catch (e : Exception) {
        e.printStackTrace()
        return 4096L * 1024 * 1024
    }
}

fun padRight(s : String, i : Int, c : Char) = s.padEnd(i,c)

fun unzip(str : InputStream, out : File, cb : (Int) -> Unit)
{
    out.mkdirs()
    val zis = ZipInputStream(str)
    try {
        var ze = zis.nextEntry
        var i = 0
        val buffer = ByteArray(1024)
        while (ze != null) {
            i++
            cb(i)
            val name = ze.name
            val newfile = out.resolve(name)
            newfile.parentFile.mkdirs()
            if(ze.isDirectory) {
                newfile.mkdir()
            } else {
                val fos = FileOutputStream(newfile)
                try {
                    var len: Int
                    do {
                        len = zis.read(buffer)
                        if (len > 0) {
                            fos.write(buffer, 0, len)
                        }
                    } while (len > 0)
                } finally {
                    fos.close()
                }
            }
            ze = zis.nextEntry
        }
    } finally {
        zis.close()
    }
}
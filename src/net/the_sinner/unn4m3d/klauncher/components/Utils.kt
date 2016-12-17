package net.the_sinner.unn4m3d.klauncher.components

import java.io.File
import java.net.URLEncoder

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
    URLEncoder.encode(it)
}.joinToString("/")
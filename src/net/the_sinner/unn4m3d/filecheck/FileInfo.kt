package net.the_sinner.unn4m3d.filecheck

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import security.SHA256
import security.SHA512
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * Created by unn4m3d on 11.12.16.
 */
data class FileInfo(val name : String, val size : Long, val sha256 : String?, val sha512 : String?)
{
    fun check(path : String, cb : (String) -> Unit) : Boolean
    {
        val info = File(path)
        if(size != info.length()) {
            cb("Wrong size : ${info.length()}, not $size")
            return false
        }
        val fis = FileInputStream(path)

        try {
            if(sha256 != null) {
                val sum = SHA256.hash(fis).toHex()
                if (sum != sha256) {
                    cb("SHA256 failed : $sum, not $sha256")
                    return false
                }
            }
            if(sha512 != null) {
                val sum = SHA512.hash(fis).toHex()
                if (sum != sha512) {
                    cb("SHA512 failed : $sum, not $sha512")
                    return false
                }
            }
        } finally {
            fis.close()
        }
        cb("OK")
        return true
    }

    override fun toString() = "#<FileInfo name=$name size=$size sha256=$sha256 sha512=$sha512>"

    fun toHash() : HashMap<String,String>
    {
        var map = HashMap<String,String>(4)

        map["name"] = name
        map["size"] = size.toString()
        map["sha256"] = sha256.toString()
        map["sha512"] = sha512.toString()

        return map
    }
}

fun fromHash(map : HashMap<String,String>) : FileInfo = FileInfo(map["name"].toString(),map["size"].toString().toLong(),map["sha256"],map["sha512"])
fun fromJson(j : String) : Map<String,FileInfo>
{
    val g = Gson()
    return g.fromJson<Map<String,FileInfo>>(j)
}

fun fromFile(path : String) : FileInfo
{
    val size = File(path).length()

    val fis = FileInputStream(path)

    try{
        val sha256 = SHA256.hash(fis).toString()
        val sha512 = SHA512.hash(fis).toString()
        return FileInfo(path,size,sha256,sha512)
    } finally {
        fis.close()
    }

}

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}
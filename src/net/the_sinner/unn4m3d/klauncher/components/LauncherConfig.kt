package net.the_sinner.unn4m3d.klauncher.components

import org.json.JSONObject
import java.io.File
import java.util.*

/**
 * Created by unn4m3d on 17.12.16.
 */

class LauncherConfig(val file : File)
{
    private var data = JSONObject()

    operator fun get(key : String) : Any{
        println("Getting $key")
        if(!data.has(key) && defaults.containsKey(key))
            data.put(key,defaults[key])

        return data[key]
    }
    operator fun set(key : String, value : Any){
        println("Setting $key to $value")
        data.put(key,value)
    }

    private var defaults = HashMap<String,Any>()

    fun <T>getOpt(key : String, def : T) : T
    {
        try {
            val value = this[key] as? T
            if (value == null)
                return def
            return value
        } catch (e : Exception) {
            e.printStackTrace()
            return def
        }
    }

    fun setDefault(key : String, value : Any)
    {
        defaults[key] = value
    }

    fun load()
    {
        data = JSONObject(file.readText())
    }

    fun save()
    {
        //println(data.toStrin)
        file.parentFile.mkdirs()
        file.writeText(data.toString())
    }

}

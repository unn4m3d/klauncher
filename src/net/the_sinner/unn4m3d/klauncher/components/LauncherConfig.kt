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
        if(!data.has(key) && defaults.containsKey(key))
            data.put(key,defaults[key])

        return data[key]
    }
    operator fun set(key : String, value : Any){
        data.put(key,value)
    }

    private var defaults = HashMap<String,Any>()

    fun <T>getOpt(key : String, def : T) : T
    {
        val value = this[key] as? T
        if(value == null)
            return def
        return value
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
        file.writeText(data.toString())
    }

}

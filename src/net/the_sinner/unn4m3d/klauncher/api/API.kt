package net.the_sinner.unn4m3d.klauncher.api

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import khttp.get
import java.util.*

/**
 * Created by unn4m3d on 15.12.16.
 */
class API(val url : String) {
    private val gson = Gson()

    fun query(path : String, params : Map<String,String>) =  get("$url$path",params = params).jsonObject

    fun auth(username : String, password : String, version : String) : SessionData
    {
        val resp = query("api/auth", mapOf(
                "username" to username,
                "password" to password,
                "version"  to version
        ))

        if(resp["error"] != null)
            throw APIException(resp.optString("error","Error"),resp.optString("errorType","unknown"))

        return SessionData(resp["username"].toString(),resp["sessionId"].toString(),resp["accessToken"].toString())
    }

    fun servers() : List<ShortServerData>
    {
        val resp = query("api/servers",HashMap<String,String>())

        if(resp["error"] != null)
            throw APIException(resp.optString("error","Error"),resp.optString("errorType","unknown"))

        val servers = resp["servers"] as JsonArray
        return servers.toList().map {
            val obj = it.asJsonObject
            ShortServerData(
                    name = obj["name"].asString,
                    shortName = obj["shortName"].asString,
                    version = obj["version"].asString,
                    ip = obj["ip"].asString,
                    port = obj["ip"].asInt
            )
        }
    }
}
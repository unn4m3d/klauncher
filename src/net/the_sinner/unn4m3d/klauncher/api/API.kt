package net.the_sinner.unn4m3d.klauncher.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import khttp.get
import net.the_sinner.unn4m3d.filecheck.FileInfo
import org.json.JSONObject
import java.util.*

/**
 * Created by unn4m3d on 15.12.16.
 */
class API(val url : String) {

    fun query(path : String, params : Map<String,String>) =  get("$url$path",params = params).jsonObject

    protected fun throw_ex(resp : JSONObject)
    {
        if(resp.has("error") && resp["error"] != null)
            throw APIException(resp.optString("error","Error"),resp.optString("errorType","unknown"))
    }

    @Throws(APIException::class)
    fun auth(username : String, password : String, version : String) : SessionData
    {
        val resp = query("api/auth", mapOf(
                "username" to username,
                "password" to password,
                "version"  to version
        ))

        throw_ex(resp)

        return SessionData(resp["username"].toString(),resp["sessionId"].toString(),resp["accessToken"].toString())
    }

    @Throws(APIException::class)
    fun servers() : List<ShortServerData>
    {
        val resp = query("api/servers",HashMap<String,String>())

        throw_ex(resp)

        val servers = resp.getJSONArray("servers")
        return servers.toList().map {
            val obj = it as Map<String,Any>
            ShortServerData(
                    name = obj["name"].toString(),
                    shortName = obj["shortName"].toString(),
                    version = obj["version"].toString(),
                    ip = obj["ip"].toString(),
                    port = obj["port"] as Int
            )
        }
    }

    @Throws(APIException::class)
    fun server(name : String) : ServerData
    {
        val resp = query("api/server", mapOf("name" to name))
        throw_ex(resp)

        var sd = ServerData(
                name = resp.optString("name","Default"),
                shortName = resp.optString("shortName","default"),
                ip = resp.getString("ip"),
                port = resp.getInt("port"),
                version = resp.getString("version"),
                online = resp.getBoolean("online"),
                motd = null,
                max_players = null,
                players = null
        )

        if(sd.online)
        {
            sd.motd = resp.getString("motd")
            sd.max_players = resp.getInt("max")
            sd.players = resp.getInt("players")
        }

        return sd
    }

    fun launcher(platform : String) = url + get("${url}api/launcher",params=mapOf("platform" to platform)).text

    @Throws(APIException::class)
    fun files(client : String, all : Boolean) : FilesData {
        val resp = query("api/files", mapOf("client" to client, "pretty" to "yes", "all" to all.toString()))
        throw_ex(resp)

        return FilesData(
                dir = resp.optString("dir", "/public/clients"),
                fileinfo = resp.getJSONArray("files").toList().map {
                    val obj = it as Map<String,String?>
                    FileInfo(
                            name = obj["name"]!!,
                            size = obj["size"]!!.toLong(),
                            sha256 = obj["sha256"]!!,
                            sha512 = obj["sha512"]!!
                    )
                }
        )
    }

}
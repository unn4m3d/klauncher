package net.the_sinner.unn4m3d.klauncher.api

/**
 * Created by unn4m3d on 15.12.16.
 */
class ServerData(
        val name : String,
        val shortName : String,
        val version : String,
        val ip : String,
        val port : Int,
        val online : Boolean,

        // Present if online
        var players : Int?,
        var max_players : Int?,
        var motd : String?
)
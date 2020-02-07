package net.the_sinner.unn4m3d.klauncher.api

/**
 * Created by unn4m3d on 15.12.16.
 */
data class ShortServerData(
        val name : String,
        val shortName : String,
        val version : String,
        val ip : String,
        val port : Int
)
{
    override fun toString() : String
    {
        return name
    }
}
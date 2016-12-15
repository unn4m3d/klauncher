package net.the_sinner.unn4m3d.klauncher.api

/**
 * Created by unn4m3d on 15.12.16.
 */


class APIException(val error : String, val errorType : String) : Exception(error)
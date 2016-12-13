package net.the_sinner.unn4m3d.filecheck

import java.io.File
import java.util.*

/**
 * Created by unn4m3d on 11.12.16.
 */

class FileChecker(val path : String, val files : HashMap<String,FileInfo>){
    fun check(ignore : Regex, callback : (State,Exception?,String?) -> Void) : ArrayList<FileResult>
    {
        callback(State.START,null,"Starting...")
        val ftw = File(path).walkTopDown()
        var _files = HashMap<String,String>()
        for(file in ftw) {
            val name = file.absolutePath.replace(path,"")
            callback(State.LISTING,null,"Adding file $name")
            _files[name] = file.absolutePath
        }

        var result = ArrayList<FileResult>()

        for(pair in files) {
            try {
                var s = "Success"
                var cr = pair.value.check(_files[pair.key].toString()) {
                    s = it
                }
                if(cr) {
                    callback(State.SUCCESS,null,"File ${_files[pair.key]} passed")
                    result.add(FileResult(pair.key,FileState.PRESENT))
                } else {
                    callback(State.FAILURE,null,s)
                    result.add(FileResult(pair.key,FileState.CORRUPT))
                }

            } catch (e : Exception) {
                callback(State.EXCEPTION,e,e.message)
                result.add(FileResult(pair.key,FileState.MISSING))
            } finally {
                if(_files.containsKey(pair.key))
                    _files.remove(pair.key)
            }

        }

        for(pair in _files) {
            if(!pair.key.matches(ignore) && !File(pair.value).isDirectory) result.add(FileResult(pair.key,FileState.UNWANTED))
        }

        callback(State.FINISH,null,"Finished")
        return result
    }
}
package net.the_sinner.unn4m3d.filecheck

import java.io.File
import java.util.*

/**
 * Created by unn4m3d on 11.12.16.
 */

class FileChecker(val path : String, val files : Map<String,FileInfo>){
    fun check(ignore : Regex, callback : (State,Exception?,String?) -> Unit) : ArrayList<FileResult>
    {
        callback(State.START,null,"Starting...")
        callback(State.START,null,"Ignore regex is /${ignore.pattern}/")
        val ftw = File(path).walkTopDown()
        var localFiles = HashMap<String,String>()
        for(file in ftw) {
            val name = file.absolutePath.replace(path, "")
            val unixName = name.replace('\\', '/')
            /*if((ignore.toString().isEmpty() || !name.matches(ignore)) && !file.isDirectory) {
                callback(State.LISTING, null, "Adding file $name")
                _files[name] = file.absolutePath
            } else {
                callback(State.LISTING, null, "Skipping file $name")
            }*/
            if(file.isDirectory)
                callback(State.LISTING, null, "Skipping directory $name")
            else {
                callback(State.LISTING, null, "Adding file $name")
                localFiles[unixName] = file.absolutePath
            
            }
        }

        var result = ArrayList<FileResult>()

        for(pair in files) {
            try {
                var s = "Success"

                if(!localFiles.containsKey(pair.key)) {
                    callback(State.FAILURE, null, "File ${pair.key} is missing")
                    result.add(FileResult(pair.key, FileState.MISSING))
                    continue
                }
                var cr = pair.value.check(localFiles[pair.key].toString()) {
                    s = it
                }
                if(cr || ignore.matches(pair.key)) {
                    callback(State.SUCCESS,null,"File ${localFiles[pair.key]} passed")
                    result.add(FileResult(pair.key,FileState.PRESENT))
                } else{

                    callback(State.FAILURE,null,s)
                    result.add(FileResult(pair.key,FileState.CORRUPT))
                }

            } catch (e : Exception) {
                callback(State.EXCEPTION,e,e.message)
                result.add(FileResult(pair.key,FileState.MISSING))
            } finally {
                if(localFiles.containsKey(pair.key))
                    localFiles.remove(pair.key)
            }
        }

        for(pair in localFiles) {
            /*if(ignore.toString().isEmpty() || !pair.key.matches(ignore) && !File(pair.value).isDirectory)*/
            if( (ignore.pattern.isEmpty() || !ignore.matches(pair.key)) && !File(pair.value).isDirectory )
                result.add(FileResult(pair.value,FileState.UNWANTED))
        }

        callback(State.FINISH,null,"Finished")
        return result
    }
}
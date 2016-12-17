package net.the_sinner.unn4m3d.klauncher.components

import net.the_sinner.unn4m3d.filecheck.FileChecker
import net.the_sinner.unn4m3d.filecheck.FileState
import net.the_sinner.unn4m3d.filecheck.State
import net.the_sinner.unn4m3d.klauncher.Config
import net.the_sinner.unn4m3d.klauncher.api.API
import java.io.File
import java.util.logging.Level
import khttp.get
import net.the_sinner.unn4m3d.filecheck.FileResult
import java.io.FileOutputStream

/**
 * Created by unn4m3d on 16.12.16.
 */

fun downloadFile(dir : File, upDir : String, name : String, cb : (Long,Long) -> Unit)
{
    val r = get("${Config.API_URL}/$upDir/${encodeRPath(name)}",stream = true)
    val size = r.headers["Content-Length"].toString().toLong()
    cb(0,size)
    val f = File(dir.absolutePath + name)
    println("[DOWNLOADING ${f.absolutePath} $size]")
    f.parentFile.mkdirs()
    f.createNewFile()
    val fis = FileOutputStream(f)
    try {
        var downloaded = 0L
        for (chunk in r.contentIterator(1024)) {
            downloaded += chunk.size
            fis.write(chunk)
            cb(downloaded, size)
        }
        println(downloaded)
    } catch (e : Exception) {
        e.printStackTrace()
    } finally {
        fis.close()
    }

}


fun checkClient(dir : File, sn : String, cb : (Level,String,Exception?) -> Unit)
{
    cb(Level.INFO, "Загрузка информации...", null)
    val info = API(Config.API_URL).files(sn,false)
    cb(Level.INFO, "Загружена информация о ${info.fileinfo.size} файлах ",null)
    cb(Level.INFO, "Проверка файлов...",null)
    val checker = FileChecker(dir.absolutePath,info.fileinfo.map{it.name to it}.toMap())
    val list = checker.check(info.ignore) { state: State, exception: Exception?, s: String? ->
        if(state == State.FAILURE || state == State.EXCEPTION) {
            cb(Level.SEVERE,s!!,exception)
        } else {
            cb(Level.INFO,s!!,null)
        }
    }

    cb(Level.INFO,"Сравнение файлов завершено",null)

    for(file in list) {
        when(file.state)
        {
            FileState.CORRUPT -> {
                cb(Level.WARNING,"Файл ${file.path} поврежден",null)
                cb(Level.INFO, "Загрузка файла ${file.path}", null)
                downloadFile(dir,info.dir,file.path) { downloaded: Long, total: Long ->
                    cb(Level.OFF,"Загрузка ${file.path} (${size(downloaded)}/${size(total)} ${downloaded/total.toFloat()*100}%)", null)
                }
            }
            FileState.MISSING -> {
                cb(Level.WARNING,"Файл ${file.path} не найден",null)
                cb(Level.INFO, "Загрузка файла ${file.path}",null)
                downloadFile(dir,info.dir,file.path) { downloaded: Long, total: Long ->
                    cb(Level.OFF,"Загрузка ${file.path} (${size(downloaded)}/${size(total)} ${downloaded/total.toFloat()*100}%)", null)
                }
            }
            FileState.UNWANTED -> {
                cb(Level.WARNING,"Файл ${file.path} будет удален",null)
                File(file.path).delete()
            }
        }
    }
}

fun downloadClient(dir : File, server : String, cb: (Level,String,Exception?) -> Unit)
{
    cb(Level.INFO,"Получение списка файлов",null)
    var files = API(Config.API_URL).files(server,true)
    cb(Level.INFO,"Список файлов получен", null)
    for(file in files.fileinfo)
    {
        cb(Level.INFO,"Загрузка файла ${file.name}", null)
        downloadFile(dir,files.dir,file.name) { d : Long, t : Long ->
            cb(Level.OFF,"Загрузка ${file.name} (${size(d)}/${size(t)} ${d/t.toFloat()*100}%)", null)
        }
    }
}

fun size(l : Long) : String
{
    var s = l
    val pref = arrayOf("K","M","G","T")
    var i = 0
    while(s > 1024)
    {
        s /= 1024
        i++
    }
    return if(i > 0){"$s ${pref[i-1]}B"}else{"$s B"}
}

fun launchUpdater(dir : File, server : String, assetsDir : File,cb: (Level,String,Exception?) -> Unit) : Boolean
{
    cb(Level.INFO,"Проверка папки клиента",null)
    var b = true
    if(dir.exists())
    {
        try {
            cb(Level.INFO, "Клиент найден", null)
            checkClient(dir, server, cb)
        }catch (e : Exception){
            b = false
            cb(Level.WARNING, e.message.toString() ,e)
        }
    }
    else
    {
        try{
        dir.mkdirs()
        cb(Level.WARNING,"Клиент не найден",null)
        downloadClient(dir,server,cb)
        } catch ( e : Exception) {
            b = false
            cb(Level.WARNING, e.message.toString(), e)
        }
    }
    return b
}
package net.the_sinner.unn4m3d.klauncher.components

import javafx.application.Platform
import net.the_sinner.unn4m3d.filecheck.FileChecker
import net.the_sinner.unn4m3d.filecheck.FileState
import net.the_sinner.unn4m3d.filecheck.State
import net.the_sinner.unn4m3d.klauncher.Config
import java.io.File
import java.util.logging.Level
import khttp.get
import net.the_sinner.unn4m3d.klauncher.api.API
import net.the_sinner.unn4m3d.klauncher.api.FilesData
//import net.the_sinner.unn4m3d.klauncher.api.apiInstance
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.util.zip.ZipEntry
import kotlin.math.roundToInt

/**
 * Created by unn4m3d on 16.12.16.
 */

fun downloadFile(dir : File, upDir : String, name : String, cb : (Long,Long) -> Unit)
{
    /* Download files over HTTP */
    val url = "${Config.API_URL}$upDir/${encodeRPath(name)}"
            .replace("https://", "http://")

    /*/val r = get(url, stream = true, headers = mapOf("Accept-Encoding" to "identity"), allowRedirects = false)
    val size = r.headers["Content-Length"].toString().toLong()
    */
    val f = File(dir.absolutePath + File.separator + name)

    val conn = URL(url).openConnection()
    val istr = conn.getInputStream()
    val size = conn.getHeaderField("Content-Length").toString().toLong()

    cb(0,size)

    println("[DOWNLOADING ${f.absolutePath} $size]")

    f.parentFile.mkdirs()
    if(f.exists())
        f.delete()
    f.createNewFile()

    //val str = r.raw
    var downloaded = 0L
    val fos = FileOutputStream(f)
    val chunk = ByteArray(2048)
    var read = 0
    try {
        while(true)
        {
            //fos.write(chunk, 0, chunk.size)

            read = istr.read(chunk, 0, chunk.size)

            if(read == -1) break

            //f.appendBytes(chunk)
            fos.write(chunk, 0, read);
            downloaded += read
            cb(downloaded, size)
        }
    } finally {
        fos.close()
    }
}


fun checkClient(apiInstance : API, dir : File, sn : String, cb : (Level,String,Exception?) -> Unit)
{
    cb(Level.INFO, "Загрузка информации...", null)
    val info = apiInstance.files(sn,false)
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
                if(File(file.path).delete())
                    cb(Level.INFO, "Файл удален",null)
                else
                    cb(Level.SEVERE, "Файл не может быть удален", null)
            }
            else -> {}
        }
    }
}

fun downloadClient(apiInstance: API, dir : File, server : String, cb: (Level,String,Exception?) -> Unit)
{
    cb(Level.INFO,"Получение списка файлов",null)
    var files = apiInstance.files(server,true)
    cb(Level.INFO,"Список файлов получен", null)
    for(file in files.fileinfo)
    {
        cb(Level.INFO,"Загрузка файла ${file.name}", null)
        downloadFile(dir,files.dir,file.name) { d : Long, t : Long ->
            var percent = "NaN"
            try {
                percent = (d/t.toFloat()*100).roundToInt().toString()
            } catch(e : Exception) {
                e.printStackTrace()
            }
            cb(Level.OFF,"Загрузка ${file.name} (${size(d)}/${size(t)} $percent%)", null)
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

fun downloadAssets(apiInstance: API, dir : File, cb: (Level,String,Exception?) -> Unit)
{
    dir.mkdirs()
    val assets = apiInstance.assets()
    /*for(asset in assets.files)
    {
        cb(Level.INFO, "Загрузка файла ${asset}", null)
        downloadFile(dir,assets.dir, asset) { d: Long, t: Long ->
            cb(Level.OFF,"Загрузка ${asset} (${size(d)}/${size(t)} ${d/t.toFloat()*100}%)", null)
        }
    }*/
    downloadFile(dir.parentFile,File(assets).parent,File(assets).name) { d: Long, t: Long ->
        var percent = "NaN"
        try {
            percent = (d/t.toFloat()*100).roundToInt().toString()
        } catch(e : Exception) {
            e.printStackTrace()
        }
        cb(Level.OFF,"Загрузка $assets (${size(d)}/${size(t)} $percent%)", null)
    }
    unzip(FileInputStream(dir.parentFile.resolve("assets.zip")),dir){ i : Int, ze : ZipEntry ->
        cb(Level.INFO,"Unpacking asset #$i : ${ze.size} bytes",null)
    }
    cb(Level.INFO, "Done unpacking", null)
}

fun launchUpdater(apiInstance: API, dir : File, server : String, assetsDir : File, forceUpd: Boolean, cb: (Level,String,Exception?) -> Unit) : Boolean
{
    cb(Level.INFO, "CD ${dir.absolutePath} AD ${assetsDir.absolutePath}",null)
    cb(Level.INFO,"Проверка папки клиента",null)
    var b = true
    if(forceUpd)
        dir.deleteRecursively()
    if(dir.exists())
    {
        try {
            cb(Level.INFO, "Клиент найден", null)
            checkClient(apiInstance, dir, server, cb)
        }catch (e : Exception){
            b = false
            cb(Level.WARNING, e.message.toString() ,e)
            e.printStackTrace()
        }
    }
    else
    {
        try{
        dir.mkdirs()
        cb(Level.WARNING,"Клиент не найден",null)
        downloadClient(apiInstance, dir,server,cb)
        } catch ( e : Exception) {
            b = false
            cb(Level.WARNING, e.message.toString(), e)
            e.printStackTrace()
        }
    }

    try {
        if (!assetsDir.exists()) downloadAssets(apiInstance, assetsDir, cb)
    } catch (e : Exception) {
        b = false
        cb(Level.WARNING, e.message.toString(), e)
        e.printStackTrace()
    }
    return b
}
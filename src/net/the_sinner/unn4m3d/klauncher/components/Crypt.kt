@file:JvmName("Crypt")

package net.the_sinner.unn4m3d.klauncher.components

import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun b64encode(str : String) = String(Base64.getEncoder().encode(str.toByteArray()))
fun b64encode(str : ByteArray) = Base64.getEncoder().encode(str)
@Throws(Exception::class)
fun b64decode(str : String) = String(Base64.getDecoder().decode(str.toByteArray()))

fun generateIV(bs : Int) : IvParameterSpec
{
    val sr = SecureRandom()
    println("BS is $bs (${bs*8} bits)")
    val bytes = ByteArray(bs)
    sr.nextBytes(bytes)
    return IvParameterSpec(bytes)
}

fun generateKey(key : String) = SecretKeySpec(
        security.SHA256.hash(key.toByteArray()).take(16).toByteArray(),
        "AES"
)

fun generateIV(iv : String) = IvParameterSpec(iv.toByteArray().take(16).toByteArray())

@Throws(Exception::class)
fun encrypt(str : String, key : String) : String
{
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val iv = generateIV(cipher.blockSize)
    cipher.init(Cipher.ENCRYPT_MODE, generateKey(key), iv)
    val enc = cipher.doFinal(str.toByteArray())
    return "${String(b64encode(iv.iv))}\$${String(b64encode(enc))}"
}

@Throws(Exception::class)
fun decrypt(str : String, key : String) : String {
    if(!str.contains('$'))
        throw Exception("No IV specified")
    val parts = str.split('$')
    val iv = generateIV(b64decode(parts[0]))
    val enc = b64decode(parts[1])

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, generateKey(key), iv)
    return String(cipher.doFinal(enc.toByteArray(Charset.forName("UTF-8"))))
}

@JvmName("xorByte")
infix fun Byte.xor(another : Byte) = (this.toInt() xor another.toInt()).toByte()

@JvmName("xorBytes")
infix fun ByteArray.xor(key : ByteArray) : ByteArray{
    val output = ByteArray(this.size)
    this.forEachIndexed { i, byte ->
        output[i] = byte xor key[i % key.size]
    }
    return output
}

@JvmName("xorStr")
infix fun String.xor(key : String) = String(this.toByteArray() xor key.toByteArray())

package net.the_sinner.unn4m3d.klauncher.components

/**
 * Created by unn4m3d on 15.12.16.
 */

fun <T,O>javaMap(item : List<T>, cb : (T) -> O ) = item.map(cb)
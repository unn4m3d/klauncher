package net.the_sinner.unn4m3d.klauncher.components

import java.awt.event.WindowEvent
import java.awt.event.WindowListener

/**
 * Created by unn4m3d on 11.12.16.
 */
class CloseWindowListener(val callback : () -> Unit) : WindowListener {
    override fun windowOpened(e: WindowEvent) {
    }

    override fun windowIconified(e: WindowEvent) {
    }

    override fun windowDeiconified(e: WindowEvent) {
    }

    override fun windowDeactivated(e: WindowEvent) {
    }

    override fun windowClosed(e: WindowEvent) {
    }

    override fun windowActivated(e: WindowEvent) {
    }

    override fun windowClosing(e: WindowEvent?) {
        callback()
    }
}
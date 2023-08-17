package com.sidharth.lg_motion.util

import com.google.android.gms.maps.model.CameraPosition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

object KMLUtils {

    fun screenOverlayImage(): String =
        """<kml xmlns="http://www.opengis.net/kml/2.2" 
            xmlns:atom="http://www.w3.org/2005/Atom"
            xmlns:gx="http://www.google.com/kml/ext/2.2">
            <Document>
            <name>LG Motion</name>
            <Folder>
            <name>Logo</name>
            <ScreenOverlay>
            <name>Logo</name>
            <Icon>
            <href>https://raw.githubusercontent.com/SidharthMudgil/lg-motion/53f29702b5b5e299da6cac01ce5c261dff40e8ee/logo.png</href>
            </Icon>
            <overlayXY x="0" y="1" xunits="fraction" yunits="fraction"/>
            <screenXY x="0.02" y="0.95" xunits="fraction" yunits="fraction"/>
            <rotationXY x="0" y="0" xunits="fraction" yunits="fraction"/>
            <size x="0.3" y="0.3" xunits="fraction" yunits="fraction"/>
            </ScreenOverlay>
            </Folder>
            </Document>
            </kml>""".trimMargin()

    fun lookAt(camera: CameraPosition): String {
        val zoom =
            156543.03392 * cos(camera.target.latitude * PI / 180) / 2.0.pow(camera.zoom.toDouble())
        return """<LookAt><longitude>${camera.target.longitude}</longitude><latitude>${camera.target.latitude}</latitude><range>${zoom * 1000}</range><tilt>${camera.tilt}</tilt><heading>${camera.bearing}</heading><gx:altitudeMode>relativeToGround</gx:altitudeMode></LookAt>"""
    }
}

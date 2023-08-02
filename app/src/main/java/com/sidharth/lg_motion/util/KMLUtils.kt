package com.sidharth.lg_motion.util

import com.google.android.gms.maps.model.CameraPosition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

object KMLUtils {

    fun screenOverlayImage(imageUrl: String, aspectRatio: Pair<Double, Double>): String =
        """<?xml version="1.0" encoding="UTF-8"?>
        <kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">
            <Document id="logo">
                <name>Smart City Dashboard</name>
                <Folder>
                    <name>Splash Screen</name>
                    <ScreenOverlay>
                        <name>Logo</name>
                        <Icon><href>$imageUrl</href></Icon>
                        <overlayXY x="0" y="1" xunits="fraction" yunits="fraction"/>
                        <screenXY x="0.02" y="0.95" xunits="fraction" yunits="fraction"/>
                        <rotationXY x="0" y="0" xunits="fraction" yunits="fraction"/>
                        <size x="${aspectRatio.first}" y="${aspectRatio.second}" xunits="fraction" yunits="fraction"/>
                    </ScreenOverlay>
                </Folder>
            </Document>
        </kml>"""

    fun lookAt(camera: CameraPosition): String {
        val zoom = 156543.03392 * cos(camera.target.latitude * PI / 180) / 2.0.pow(camera.zoom.toDouble())
        return """<LookAt><longitude>${camera.target.longitude}</longitude><latitude>${camera.target.latitude}</latitude><range>${zoom * 1000}</range><tilt>${camera.tilt}</tilt><heading>${camera.bearing}</heading><gx:altitudeMode>relativeToGround</gx:altitudeMode></LookAt>"""
    }
}

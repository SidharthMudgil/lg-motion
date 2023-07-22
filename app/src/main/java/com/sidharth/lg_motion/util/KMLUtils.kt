package com.sidharth.lg_motion.util

import com.google.android.gms.maps.model.CameraPosition

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

    fun lookAtLinear(
        latitude: Double,
        longitude: Double,
        zoom: Double,
        tilt: Double,
        bearing: Double
    ): String =
        """<LookAt><longitude>$longitude</longitude><latitude>$latitude</latitude><range>$zoom</range><tilt>$tilt</tilt><heading>$bearing</heading><gx:altitudeMode>relativeToGround</gx:altitudeMode></LookAt>"""

    private fun lookAt(camera: CameraPosition, scaleZoom: Boolean): String =
        """<LookAt>
            <longitude>${camera.target.longitude}</longitude>
            <latitude>${camera.target.latitude}</latitude>
            <range>${if (scaleZoom) camera.zoom else camera.zoom}</range>
            <tilt>${camera.tilt}</tilt>
            <heading>${camera.bearing}</heading>
            <gx:altitudeMode>relativeToGround</gx:altitudeMode>
        </LookAt>"""

//    TODO()
    /*    fun buildOrbit(cityDataProvider: DataProvider, lastGMapPositionProvider: LastGMapPositionProvider): String {
            var lookAts = ""

            for (location in cityDataProvider.availableTours) {
                lookAts += """<gx:FlyTo>
                    <gx:duration>5.0</gx:duration>
                    <gx:flyToMode>bounce</gx:flyToMode>
                    ${lookAt(CameraPosition(location, 16f, 30f), true)}
                </gx:FlyTo>
                """
            }

            lookAts += """<gx:FlyTo>
                <gx:duration>5.0</gx:duration>
                <gx:flyToMode>bounce</gx:flyToMode>
                ${lookAt(lastGMapPositionProvider.get(), false)}
            </gx:FlyTo>
            """

            return """<?xml version="1.0" encoding="UTF-8"?>
            <kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">
                <gx:Tour>
                    <name>Orbit</name>
                    <gx:Playlist>
                        $lookAts
                    </gx:Playlist>
                </gx:Tour>
            </kml>
            """
        }*/
}

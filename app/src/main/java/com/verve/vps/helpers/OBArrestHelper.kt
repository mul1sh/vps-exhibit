package com.verve.vps.helpers

object OBArrestHelper {

    @JvmStatic
    internal var arrestingOfficersNames : MutableList<Int> = ArrayList()

    @JvmStatic
    internal var arrestingOfficersIds : MutableList<Int> = ArrayList()

    @JvmStatic
    internal lateinit  var arrestLocation : String

    @JvmStatic
    internal lateinit  var fingerPrintBase64 : String

}
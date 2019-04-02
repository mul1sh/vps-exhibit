package com.verve.vps.helpers

object OBReportHelper {
    @JvmStatic
    internal  var viewOBOccurrenceId: String = ""

    @JvmStatic
    internal var assignedOBOfficers : MutableList<Int> = ArrayList()

    @JvmStatic
    internal var linkedOccurrences : MutableList<Int> = ArrayList()

    @JvmStatic
    internal lateinit  var linkedExhibitId : String

    @JvmStatic
    internal lateinit  var linkedOccurrenceId : String

    @JvmStatic
    internal lateinit  var linkedOBNumber : String

}
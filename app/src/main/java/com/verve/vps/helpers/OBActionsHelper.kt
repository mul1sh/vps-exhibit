package com.verve.vps.helpers

import com.verve.vps.models.Exhibit
import com.verve.vps.models.Occurrence

object OBActionsHelper {

    @JvmStatic
    internal lateinit var relatedExhibits: MutableList<Exhibit>

    @JvmStatic
    internal lateinit var relatedOccurrences: MutableList<Occurrence>

}
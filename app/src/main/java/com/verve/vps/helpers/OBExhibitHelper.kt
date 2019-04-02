package com.verve.vps.helpers

import android.net.Uri

object OBExhibitHelper {

    @JvmStatic
    internal var attachedExhibitUri: Uri? = null

    @JvmStatic
    internal var imageSelectedFromFile = false

    @JvmStatic
    internal var imageSelectedFromCamera = false

    @JvmStatic
    internal var selectedExhibitCategory: Int? = null

    @JvmStatic
    internal var selectedOIC: Int? = null

    @JvmStatic
    internal var selectedItemCategory: Int? = null

    @JvmStatic
    internal var selectedUnit: Int? = null

    @JvmStatic
    internal var itemType: String? = null

    @JvmStatic
    internal var serial: String? = null

    @JvmStatic
    internal var quantity: String? = null

    @JvmStatic
    internal var desc: String? = null

    @JvmStatic
    internal lateinit var selectedExhibitIdFromList: String




}

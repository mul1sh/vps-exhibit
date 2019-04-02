package com.verve.vps.helpers

import com.verve.vps.plugins.CustomViewPager

object Constants {

    // pref strings
    internal const val PREFS_NAME = "MY_PREFS"

    // for login
    internal const val AUTH_TOKEN                                = "AUTH_TOKEN"
    internal const val USER_LOGGED_IN                            = "USER_LOGGED_IN"
    internal const val LOGIN_STATUS                              = "LOGIN_STATUS"
    internal const val LOGIN_USER_DATA                           = "LOGIN_USER_DATA"
    internal const val LOGIN_TOKEN                               = "LOGIN_TOKEN"
    internal const val OFFICER_NAME                              = "OFFICER_NAME"
    internal const val OFFICER_SERVICE_NO                        = "OFFICER_SERVICE_NO"
    internal const val OFFICER_RANK                              = "OFFICER_RANK"

    // for ob report form
    internal const val REPORTER_ID_PASSPORT_NUMBER               = "REPORTER_ID_PASSPORT_NUMBER"
    internal const val REPORTER_NAME                             = "REPORTER_NAME"
    internal const val REPORTER_PHONE_NUMBER                     = "REPORTER_PHONE_NUMBER"
    internal const val REPORTER_GENDER                           = "REPORTER_GENDER"
    internal const val REPORTER_COUNTY_ID                        = "REPORTER_COUNTY_ID"
    internal const val REPORTER_COUNTY                           = "REPORTER_COUNTY"
    internal const val REPORTER_SUB_COUNTY_ID                    = "REPORTER_SUB_COUNTY_ID"
    internal const val REPORTER_CONSTITUENCY                     = "REPORTER_CONSTITUENCY"
    internal const val REPORTER_NATIONALITY                      = "REPORTER_NATIONALITY"
    internal const val REPORTER_GENDER_SELECTED_POSITION         = "REPORTER_GENDER_SELECTED_POSITION"
    internal const val REPORTER_COUNTY_SELECTED_POSITION         = "REPORTER_COUNTY_SELECTED_POSITION"
    internal const val REPORTER_CONSTITUENCY_SELECTED_POSITION   = "REPORTER_CONSTITUENCY_SELECTED_POSITION"
    internal const val REPORTER_FORM_FILLED                      = "REPORTER_FORM_FILLED"
    internal const val GO_BACK_TO_REPORTER_FORM                  = "GO_BACK_TO_REPORTER_FORM"


    // for ob occurrence details
    internal const val OCCURRENCE_HEADING                        = "OCCURRENCE_HEADING"
    internal const val OCCURRENCE_NARRATIVE                      = "OCCURRENCE_NARRATIVE"
    internal const val OCCURRENCE_LATITUDE                       = "OCCURRENCE_LATITUDE"
    internal const val OCCURRENCE_LONGITUDE                      = "OCCURRENCE_LONGITUDE"
    internal const val OCCURRENCE_PLACE_NAME                     = "OCCURRENCE_PLACE_NAME"

    // for view ob
    internal const val OCCURRENCE_NO                             = "OCCURRENCE_NO"
    
    // for ob arrest form
    internal const val ARRESTEE_ID_PASSPORT_NUMBER               = "ARRESTEE_ID_PASSPORT_NUMBER"
    internal const val ARRESTEE_NAME                             = "ARRESTEE_NAME"
    internal const val ARRESTEE_PHONE_NUMBER                     = "ARRESTEE_PHONE_NUMBER"
    internal const val ARRESTEE_GENDER                           = "ARRESTEE_GENDER"
    internal const val ARRESTEE_COUNTY                           = "ARRESTEE_COUNTY"
    internal const val ARRESTEE_COUNTY_ID                        = "ARRESTEE_COUNTY_ID"
    internal const val ARRESTEE_SUB_COUNTY_ID                    = "ARRESTEE_SUB_COUNTY_ID"
    internal const val ARRESTEE_CONSTITUENCY                     = "ARRESTEE_CONSTITUENCY"
    internal const val ARRESTEE_NATIONALITY                      = "ARRESTEE_NATIONALITY"
    internal const val ARRESTEE_KIN_NAME                         = "ARRESTEE_KIN_NAME"
    internal const val ARRESTEE_KIN_PHONE_NUMBER                 = "AARRESTEE_KIN_PHONE_NUMBER"
    internal const val ARRESTEE_KIN_RELATIONSHIP                 = "ARRESTEE_KIN_RELATIONSHIP"
    internal const val ARRESTEE_KIN_RELATIONSHIP_ID              = "ARRESTEE_KIN_RELATIONSHIP_ID"
    internal const val ARRESTEE_GENDER_SELECTED_POSITION         = "ARRESTEE_GENDER_SELECTED_POSITION"
    internal const val ARRESTEE_COUNTY_SELECTED_POSITION         = "ARRESTEE_COUNTY_SELECTED_POSITION"
    internal const val ARRESTEE_CONSTITUENCY_SELECTED_POSITION   = "ARRESTEE_CONSTITUENCY_SELECTED_POSITION"
    internal const val ARRESTEE_RELATIONSHIP_SELECTED_POSITION   = "ARRESTEE_RELATIONSHIP_SELECTED_POSITION"
    internal const val ARRESTEE_FORM_FILLED                      = "ARRESTEE_FORM_FILLED"
    internal const val GO_BACK_TO_ARRESTEE_FORM                  = "GO_BACK_TO_ARRESTEE_FORM"
    internal const val GO_BACK_TO_BIOMETRIC_FORM                 = "GO_BACK_TO_BIOMETRIC_FORM"
    

    // custom view pager
    @JvmStatic
    internal lateinit var customViewPager: CustomViewPager


}
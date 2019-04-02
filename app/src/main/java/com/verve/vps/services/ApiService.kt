package com.verve.vps.services

import com.google.gson.JsonPrimitive

import com.verve.vps.models.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody


import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/login")
    fun loginUser(@Header("Authorization") authToken: String): Observable<LoginResult>


    @FormUrlEncoded
    @POST("/api/change-password")
    fun changePassword(@Header("Authorization") authToken: String,
                       @Field("question") question: String,
                       @Field("answer") answer: String,
                       @Field("newpassword") newPassword: String): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("/api/firstlogin")
    fun firstTimeLogin(@Header("Authorization") authToken: String,
                       @Field("question") question: String,
                       @Field("answer") answer: String,
                       @Field("newpassword") newPassword: String): Observable<Response<JsonPrimitive>>


    @GET("/api/person/{userIdNumber}")
    fun searchPerson(@Header("Authorization") authToken: String,
                     @Path("userIdNumber") userId: String): Observable<SearchPersonResult>


    @FormUrlEncoded
    @POST("/api/report")
    fun saveOccurrence(@Header("Authorization") authToken: String,
                       @Field("id_number") id_number: String,
                       @Field("name") name: String,
                       @Field("phone_number") phoneNumber: String,
                       @Field("county_id") countyId: String,
                       @Field("sub_county_id") subCountyId: String,
                       @Field("gender") gender: String,
                       @Field("description") description: String,
                       @Field("narrative") narrative: String,
                       @Field("area") area: String,
                       @Field("lat") latitude: String,
                       @Field("lon") longitude: String): Observable<SaveOccurrenceResult>

    @GET("/api/occurrences")
    fun getOccurrences(@Header("Authorization") authToken: String): Observable<OccurrencesListResult>

    @GET("/api/report/{occurrenceId}")
    fun getOccurrenceDetails(@Header("Authorization") authToken: String,
                             @Path("occurrenceId") occurrenceId: String): Observable<OccurrenceReportDetailsResult>

    @Streaming
    @GET("api/generate-abstract/{occurrenceId}")
    fun downloadAbstract(@Header("Authorization") authToken: String,
                         @Path("occurrenceId") occurrenceId: String): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("api/email-abstract/{occurrenceId}")
    fun emailAbstract(@Header("Authorization") authToken: String,
                      @Field("email") email: String,
                      @Path("occurrenceId") occurrenceId: String): Observable<Response<ResponseBody>>

    @GET("/api/resolve-occurrence/{occurrenceId}")
    fun resolveOccurrence(@Header("Authorization") authToken: String,
                          @Path("occurrenceId") occurrenceId: String): Observable<Response<ResponseBody>>

    @GET("/api/station-officers")
    fun getOfficersListingAsJson(@Header("Authorization") authToken: String): Observable<Response<ResponseBody>>

    @GET("/api/station-officers")
    fun getOfficersListingAsObject(@Header("Authorization") authToken: String): Observable<OfficersListing>

    @POST("/api/assign-case-officers/{occurrenceId}")
    fun assignOfficersToOccurrence(@Header("Authorization") authToken: String,
                                   @Body assignedOfficers: AssignOfficers,
                                   @Path("occurrenceId") occurrenceId: String): Observable<Response<ResponseBody>>

    @GET("/api/forward-to-dcio/{occurrenceId}")
    fun forwardToDCIO(@Header("Authorization") authToken: String,
                      @Path("occurrenceId") occurrenceId: String): Observable<Response<ResponseBody>>

    @POST("/api/add-related-occurrences/{occurrenceId}")
    fun linkOccurrences(@Header("Authorization") authToken: String,
                        @Body linkOccurrences: LinkOccurrences,
                        @Path("occurrenceId") occurrenceId: String): Observable<Response<ResponseBody>>


    @GET("/api/exhibits")
    fun getExhibits(@Header("Authorization") authToken: String): Observable<ExhibitListResult>

    @FormUrlEncoded
    @POST("/api/link-exhibit-to-occurrence/{occurrenceId}")
    fun linkExhibitToOccurrence(@Header("Authorization") authToken: String,
                                @Field("related") related: String,
                                @Path("occurrenceId") occurrenceId: String): Observable<Response<ResponseBody>>


    @GET("/api/related-occurrences/{occurrenceId}")
    fun getRelatedOccurrences(@Header("Authorization") authToken: String,
                              @Path("occurrenceId") occurrenceId: String): Observable<RelatedOccurrencesResult>


    @GET("/api/related-exhibits/{occurrenceId}")
    fun getRelatedExhibits(@Header("Authorization") authToken: String,
                           @Path("occurrenceId") occurrenceId: String): Observable<ExhibitListResult>

    @Multipart
    @POST("/api/exhibit")
    fun saveExhibit(@Header("Authorization") authToken: String,
                    @Part("exhibit_category") exhibit_category: RequestBody,
                    @Part("category") category: RequestBody,
                    @Part("type") type: RequestBody,
                    @Part("quantity") quantity: RequestBody,
                    @Part("unit") unit: RequestBody,
                    @Part("serial_number") serial_number: RequestBody,
                    @Part("description") description: RequestBody,
                    @Part("officer_in_charge") officer_in_charge: RequestBody,
                    @Part("station_id") station_id: RequestBody,
                    @Part exhibit_image: MultipartBody.Part):  Observable<Response<ResponseBody>>


    @GET("/api/exhibit/{exhibitId}")
    fun getExhibitDetails(@Header("Authorization") authToken: String,
                          @Path("exhibitId") exhibitId: String): Observable<ExhibitDetailsResult>


    @FormUrlEncoded
    @POST("/api/exhibit-transfer/{exhibitId}")
    fun transferExhibit(@Header("Authorization") authToken: String,
                        @Field("destination") destination: String,
                        @Field("remarks") remarks: String,
                        @Path("exhibitId") exhibitId: String): Observable<Response<ResponseBody>>


    @GET("/api/exhibit-readmit/{exhibitId}")
    fun reAdmitExhibit(@Header("Authorization") authToken: String,
                          @Path("exhibitId") exhibitId: String): Observable<Response<ResponseBody>>

}
package com.mezyapps.bni_visitor.api_common;


import com.mezyapps.bni_visitor.model.SuccessModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST(EndApi.WS_LOGIN)
    @FormUrlEncoded
    Call<SuccessModel> login(@Field("username") String mobile_no,
                             @Field("password") String password);


    @POST(EndApi.WS_CHANGE_PASSWORD)
    @FormUrlEncoded
    Call<SuccessModel> changePassword(@Field("user_id") String user_id,
                                      @Field("password") String password);

    @POST(EndApi.WS_ADD_CHAPTER)
    @FormUrlEncoded
    Call<SuccessModel> addChapterName(@Field("chapter_name") String chapter_name);

    @POST(EndApi.WS_ADD_LAUNCH_DC)
    @FormUrlEncoded
    Call<SuccessModel> addLaunchDp(@Field("launch_dc") String launch_dc);


    @POST(EndApi.WS_LIST_CHAPTER)
    Call<SuccessModel> chapterList();

    @POST(EndApi.WS_LUNCH_DC)
    Call<SuccessModel> launchDcList();

    @POST(EndApi.WS_VISITOR)
    @FormUrlEncoded
    Call<SuccessModel> addVisitor(@Field("name") String name,
                                  @Field("mobile_number") String mobile_number,
                                  @Field("email") String email,
                                  @Field("category") String category,
                                  @Field("location") String location,
                                  @Field("chapter_name") String chapter_name,
                                  @Field("source") String source,
                                  @Field("person_name") String person_name,
                                  @Field("status") String status,
                                  @Field("current_date") String current_date,
                                  @Field("follow_up_date") String follow_up_date,
                                  @Field("launch_dc") String launch_dc,
                                  @Field("description") String description,
                                  @Field("followup_date") String followDateSend);

    @POST(EndApi.WS_EDIT_VISITOR)
    @FormUrlEncoded
    Call<SuccessModel> editVisitor(@Field("visitor_id") String visitor_id,
                                   @Field("status") String status,
                                   @Field("follow_up_date") String follow_up_date,
                                   @Field("launch_dc") String launch_dc,
                                   @Field("description") String description,
                                   @Field("insert_date") String insert_date);


    @POST(EndApi.WS_COUNT_PIE)
    Call<SuccessModel> pieChartCount();

    @POST(EndApi.WS_VISITOR_LIST)
    Call<SuccessModel> visitorAllList();

    @POST(EndApi.WS_VISITOR_LIST_STATUS)
    @FormUrlEncoded
    Call<SuccessModel> visitorListStatus(@Field("status") String status);

    @POST(EndApi.WS_VISITOR_SOURCE)
    @FormUrlEncoded
    Call<SuccessModel> visitorListSource(@Field("source") String source,
                                         @Field("status") String status);

    @POST(EndApi.WS_VISITOR_CHAPTER)
    @FormUrlEncoded
    Call<SuccessModel> visitorListChapter(@Field("chapter") String chapter,
                                          @Field("status") String status);


    @POST(EndApi.WS_SINGLE_DATE_FILTER)
    @FormUrlEncoded
    Call<SuccessModel> singleDateFilter(@Field("date") String date);

    @POST(EndApi.WS_TWO_DATE_FILTER)
    @FormUrlEncoded
    Call<SuccessModel> twoDateFilter(@Field("startdate") String startdate,
                                     @Field("enddate") String enddate);

    @POST(EndApi.WS_EDIT_CHAPTER)
    @FormUrlEncoded
    Call<SuccessModel> editChapter(@Field("chapter_id") String chapter_id,
                                   @Field("chapter_name") String chapter_name);

    @POST(EndApi.WS_EDIT_LAUNCH_DC)
    @FormUrlEncoded
    Call<SuccessModel> editLaunchDc(@Field("launch_dc_id") String launch_dc_id,
                                    @Field("launch_dc_name") String launch_dc_name);

    @POST(EndApi.WS_CHAPTER_PIE_COUNT)
    @FormUrlEncoded
    Call<SuccessModel> chapterPieCount(@Field("chapter_id") String chapter_id);

    @POST(EndApi.WS_SOURCE_PIE_COUNT)
    @FormUrlEncoded
    Call<SuccessModel> sourcePieCount(@Field("source") String source);


    @POST(EndApi.WS_VISITOR_HISTORY)
    @FormUrlEncoded
    Call<SuccessModel> visitorHistory(@Field("visitor_id") String visitor_id);

    @POST(EndApi.WS_VISITOR_LAUNCH_DC_COUNT)
    @FormUrlEncoded
    Call<SuccessModel> visitorBYLaunchDcCount(@Field("launch_dc_id") String launch_dc_id);

    @POST(EndApi.WS_VISITOR_LAUNCH_DC)
    @FormUrlEncoded
    Call<SuccessModel> visitorListLaunchDc(@Field("launch_dc_id") String launch_dc_id,
                                           @Field("status") String status);

    @POST(EndApi.WS_DELETE_VISITOR)
    @FormUrlEncoded
    Call<SuccessModel> deleteVisitor(@Field("visitor_id") String visitor_id);

    @POST(EndApi.WS_UPDATE_VISITOR)
    @FormUrlEncoded
    Call<SuccessModel> updateVisitor(@Field("visitor_id") String visitor_id,
                                     @Field("name") String name,
                                     @Field("mobile") String mobile,
                                     @Field("email") String email,
                                     @Field("category") String category,
                                     @Field("location") String location,
                                     @Field("chapter") String chapter,
                                     @Field("source") String source,
                                     @Field("launch_dc") String launch_dc,
                                     @Field("description") String description);


}

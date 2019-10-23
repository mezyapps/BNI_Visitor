package com.mezyapps.bni_visitor.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SuccessModel {

    private String message;
    private String code;

    private String not_interested,member,follow_up;


    @SerializedName("admin")
    private ArrayList<AdminLoginModel> adminLoginModelArrayList;

    @SerializedName("chapter_list")
    private ArrayList<ChapterListModel> chapterListModelArrayList;

    @SerializedName("launch_dc_list")
    private ArrayList<LunchDcModel> lunchDcModelArrayList;

    @SerializedName("visitor_list")
    private ArrayList<VisitorListAllModel> visitorListAllModelArrayList;

    @SerializedName("visitor_list_status")
    private ArrayList<VisitorListStatusModel> visitorListStatusModelArrayList;

    @SerializedName("visitor_list_source")
    private ArrayList<VisitorBySourceModel> visitorBySourceModelArrayList;


    @SerializedName("visitor_list_chapter")
    private ArrayList<VisitorByChapterModel> visitorByChapterModelArrayList;

    @SerializedName("visitor_date_filter")
    private ArrayList<VisitorDateFilterModel> visitorDateFilterModelArrayList;

    @SerializedName("visitor_history_list")
    private ArrayList<VisitorHistoryModel> visitorHistoryModelArrayList;


    public ArrayList<VisitorHistoryModel> getVisitorHistoryModelArrayList() {
        return visitorHistoryModelArrayList;
    }

    public void setVisitorHistoryModelArrayList(ArrayList<VisitorHistoryModel> visitorHistoryModelArrayList) {
        this.visitorHistoryModelArrayList = visitorHistoryModelArrayList;
    }

    public ArrayList<VisitorDateFilterModel> getVisitorDateFilterModelArrayList() {
        return visitorDateFilterModelArrayList;
    }

    public void setVisitorDateFilterModelArrayList(ArrayList<VisitorDateFilterModel> visitorDateFilterModelArrayList) {
        this.visitorDateFilterModelArrayList = visitorDateFilterModelArrayList;
    }

    public ArrayList<VisitorByChapterModel> getVisitorByChapterModelArrayList() {
        return visitorByChapterModelArrayList;
    }

    public void setVisitorByChapterModelArrayList(ArrayList<VisitorByChapterModel> visitorByChapterModelArrayList) {
        this.visitorByChapterModelArrayList = visitorByChapterModelArrayList;
    }

    public ArrayList<VisitorBySourceModel> getVisitorBySourceModelArrayList() {
        return visitorBySourceModelArrayList;
    }

    public void setVisitorBySourceModelArrayList(ArrayList<VisitorBySourceModel> visitorBySourceModelArrayList) {
        this.visitorBySourceModelArrayList = visitorBySourceModelArrayList;
    }

    public ArrayList<VisitorListAllModel> getVisitorListAllModelArrayList() {
        return visitorListAllModelArrayList;
    }

    public void setVisitorListAllModelArrayList(ArrayList<VisitorListAllModel> visitorListAllModelArrayList) {
        this.visitorListAllModelArrayList = visitorListAllModelArrayList;
    }

    public ArrayList<VisitorListStatusModel> getVisitorListStatusModelArrayList() {
        return visitorListStatusModelArrayList;
    }

    public void setVisitorListStatusModelArrayList(ArrayList<VisitorListStatusModel> visitorListStatusModelArrayList) {
        this.visitorListStatusModelArrayList = visitorListStatusModelArrayList;
    }

    public String getNot_interested() {
        return not_interested;
    }

    public void setNot_interested(String not_interested) {
        this.not_interested = not_interested;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getFollow_up() {
        return follow_up;
    }

    public void setFollow_up(String follow_up) {
        this.follow_up = follow_up;
    }

    public ArrayList<LunchDcModel> getLunchDcModelArrayList() {
        return lunchDcModelArrayList;
    }

    public void setLunchDcModelArrayList(ArrayList<LunchDcModel> lunchDcModelArrayList) {
        this.lunchDcModelArrayList = lunchDcModelArrayList;
    }

    public ArrayList<ChapterListModel> getChapterListModelArrayList() {
        return chapterListModelArrayList;
    }

    public void setChapterListModelArrayList(ArrayList<ChapterListModel> chapterListModelArrayList) {
        this.chapterListModelArrayList = chapterListModelArrayList;
    }



    public ArrayList<AdminLoginModel> getAdminLoginModelArrayList() {
        return adminLoginModelArrayList;
    }

    public void setAdminLoginModelArrayList(ArrayList<AdminLoginModel> adminLoginModelArrayList) {
        this.adminLoginModelArrayList = adminLoginModelArrayList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

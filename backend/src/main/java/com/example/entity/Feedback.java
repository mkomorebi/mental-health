package com.example.entity;

/**
 * 反馈类，表示用户的反馈信息。
 */
public class Feedback {
    private Integer id; // 反馈ID
    private Integer userId; // 用户ID
    private String question; // 反馈问题
    private String content; // 反馈内容
    private String time; // 反馈时间
    private String replyName; // 回复者姓名
    private String replyContent; // 回复内容
    private String replyTime; // 回复时间
    private String status; // 反馈状态
    private String userName; // 用户姓名
    
    // 新增字段
    private String type; // 反馈类型：feature(功能建议)、bug(问题反馈)、question(使用咨询)、other(其他)
    private Integer urgency; // 紧急程度：0-100的整数值
    private String imageUrls; // 图片URL，多个URL用逗号分隔

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // 新增字段的getter和setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUrgency() {
        return urgency;
    }

    public void setUrgency(Integer urgency) {
        this.urgency = urgency;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }
}

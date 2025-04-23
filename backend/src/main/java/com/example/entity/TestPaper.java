package com.example.entity;

import java.util.List;
import lombok.Data;

/**
 * 试卷类，表示试卷信息。
 */
@Data
public class TestPaper {
    private Integer id; // 试卷ID
    private String title; // 试卷标题
    private String img; // 试卷封面图
    private String content; // 试卷内容
    private Integer typeId; // 试卷类型ID
    private Integer doctorId; // 医生ID
    private Integer num; // 题目数量
    private Integer score; // 总分
    private String ids; // 题目ID列表
    private String status; // 状态
    private Integer testNum; // 测试次数
    private String time; // 发布时间
    private String aRange; // A选项范围
    private String bRange; // B选项范围
    private String cRange; // C选项范围
    private String aAnswer; // A选项答案
    private String bAnswer; // B选项答案
    private String cAnswer; // C选项答案

    private String typeName; // 试卷类型名称
    private String doctorName; // 医生姓名
    private String doctorAvatar; // 医生头像
    private List<Integer> idList; // 题目ID列表
    private List<Topic> topicList; // 题目列表
    private Integer aLeft; // A选项左边界
    private Integer aRight; // A选项右边界
    private Integer bLeft; // B选项左边界
    private Integer bRight; // B选项右边界
    private Integer cLeft; // C选项左边界
    private Integer cRight; // C选项右边界
    private Integer flag; // 标记
    private Integer companyId; // 添加公司ID字段

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTestNum() {
        return testNum;
    }

    public void setTestNum(Integer testNum) {
        this.testNum = testNum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getaRange() {
        return aRange;
    }

    public void setaRange(String aRange) {
        this.aRange = aRange;
    }

    public String getbRange() {
        return bRange;
    }

    public void setbRange(String bRange) {
        this.bRange = bRange;
    }

    public String getcRange() {
        return cRange;
    }

    public void setcRange(String cRange) {
        this.cRange = cRange;
    }

    public String getaAnswer() {
        return aAnswer;
    }

    public void setaAnswer(String aAnswer) {
        this.aAnswer = aAnswer;
    }

    public String getbAnswer() {
        return bAnswer;
    }

    public void setbAnswer(String bAnswer) {
        this.bAnswer = bAnswer;
    }

    public String getcAnswer() {
        return cAnswer;
    }

    public void setcAnswer(String cAnswer) {
        this.cAnswer = cAnswer;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorAvatar() {
        return doctorAvatar;
    }

    public void setDoctorAvatar(String doctorAvatar) {
        this.doctorAvatar = doctorAvatar;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;
    }

    public Integer getaLeft() {
        return aLeft;
    }

    public void setaLeft(Integer aLeft) {
        this.aLeft = aLeft;
    }

    public Integer getaRight() {
        return aRight;
    }

    public void setaRight(Integer aRight) {
        this.aRight = aRight;
    }

    public Integer getbLeft() {
        return bLeft;
    }

    public void setbLeft(Integer bLeft) {
        this.bLeft = bLeft;
    }

    public Integer getbRight() {
        return bRight;
    }

    public void setbRight(Integer bRight) {
        this.bRight = bRight;
    }

    public Integer getcLeft() {
        return cLeft;
    }

    public void setcLeft(Integer cLeft) {
        this.cLeft = cLeft;
    }

    public Integer getcRight() {
        return cRight;
    }

    public void setcRight(Integer cRight) {
        this.cRight = cRight;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}

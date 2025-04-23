package com.example.entity;

/**
 * 题目类，表示题库中的题目信息。
 */
public class Topic {
    private Integer id; // 题目ID
    private String title; // 题目标题
    private Integer typeId; // 题目类型ID
    private String aName; // A选项名称
    private String bName; // B选项名称
    private String cName; // C选项名称
    private String dName; // D选项名称
    private Integer aScore; // A选项分数
    private Integer bScore; // B选项分数
    private Integer cScore; // C选项分数
    private Integer dScore; // D选项分数
    private Integer score; // 题目总分
    private Integer companyId; // 公司ID

    private String typeName; // 题目类型名称
    private String userAnswer; // 用户答案

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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public Integer getaScore() {
        return aScore;
    }

    public void setaScore(Integer aScore) {
        this.aScore = aScore;
    }

    public Integer getbScore() {
        return bScore;
    }

    public void setbScore(Integer bScore) {
        this.bScore = bScore;
    }

    public Integer getcScore() {
        return cScore;
    }

    public void setcScore(Integer cScore) {
        this.cScore = cScore;
    }

    public Integer getdScore() {
        return dScore;
    }

    public void setdScore(Integer dScore) {
        this.dScore = dScore;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}

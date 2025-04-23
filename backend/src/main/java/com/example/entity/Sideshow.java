package com.example.entity;

/**
 * 轮播图类，表示轮播图信息。
 */
public class Sideshow {
    private Integer id; // 轮播图ID
    private String img; // 图片链接
    private Integer propagateId; // 关联的宣传ID
    private String propagateTitle; // 关联宣传的标题
    private transient Integer companyId; // 公司ID，仅用于查询条件，不存储到数据库

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getPropagateId() {
        return propagateId;
    }

    public void setPropagateId(Integer propagateId) {
        this.propagateId = propagateId;
    }

    public String getPropagateTitle() {
        return propagateTitle;
    }

    public void setPropagateTitle(String propagateTitle) {
        this.propagateTitle = propagateTitle;
    }
    
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}

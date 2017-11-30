package com.onefamily.ucenter.model;

import com.onefamily.common.enums.SexEnum;
import com.onefamily.common.enums.UserStateEnum;
import com.onefamily.common.enums.YesNoEnum;
import com.onefamily.platform.sqls.mapping.annotations.Column;
import com.onefamily.platform.sqls.mapping.annotations.Table;

import java.io.Serializable;
import java.util.Date;

@Table(name = "t_user")
public class User implements Serializable {

    @Column(sequence = "seq_t_user_id", isPrimaryKey = true)
    private Long id;
    private String nickName;
    private String headimgurl = "http://www.toysandco.com/media/products/large/HEE3500-L.jpg";
    private Integer sex = SexEnum.UNKNOWN.getValue();
    private String mobile;
    private Integer certificationYn = YesNoEnum.NO.getCode();
    private String cardNo;
    private String realName;
    private String unionid;
    private Integer status = UserStateEnum.NORMAL.getValue();
    private String appToken;
    private String wxToken;
    private Date createdDate;
    private Date modiDate;
    private String comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getCertificationYn() {
        return certificationYn;
    }

    public void setCertificationYn(Integer certificationYn) {
        this.certificationYn = certificationYn;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getWxToken() {
        return wxToken;
    }

    public void setWxToken(String wxToken) {
        this.wxToken = wxToken;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModiDate() {
        return modiDate;
    }

    public void setModiDate(Date modiDate) {
        this.modiDate = modiDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

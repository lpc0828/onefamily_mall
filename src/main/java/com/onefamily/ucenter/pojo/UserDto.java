package com.onefamily.ucenter.pojo;

import com.onefamily.common.enums.SexEnum;
import com.onefamily.common.enums.UserStateEnum;
import com.onefamily.common.enums.YesNoEnum;

import java.io.Serializable;
import java.util.Date;


public class UserDto implements Serializable {

    private static final long serialVersionUID = -8240651573352127728L;

    private Long id;
    private String nickName;
    private String headimgurl;
    private String sex = SexEnum.UNKNOWN.getCode();
    private String mobile;
    private String certificationYn = YesNoEnum.NO.getValue();
    private String cardNo;
    private String realName;
    private String unionid;
    private String status = UserStateEnum.NORMAL.getCode();
    private String utoken;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCertificationYn() {
        return certificationYn;
    }

    public void setCertificationYn(String certificationYn) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUtoken() {
        return utoken;
    }

    public void setUtoken(String utoken) {
        this.utoken = utoken;
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

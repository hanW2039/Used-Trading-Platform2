package com.wsk.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserInformation implements Serializable {
    private Integer id;

    private Date modified;

    private String username;

    /**
     * 新加
     */
    private String phone;

    private String email;

    private String password;

    private Integer type;

    private Integer status;

    private String confirmPassword;

    private String realname;

    private String clazz;

    private String sno;

    private String dormitory;

    private String gender;

    private Date createtime;

    private String avatar;

    public Date getModified() {
        return modified == null ? null : (Date) modified.clone();
    }

    public void setModified(Date modified) {
        this.modified = modified == null ? null : (Date) modified.clone();
    }


    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }


    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }


    public void setClazz(String clazz) {
        this.clazz = clazz == null ? null : clazz.trim();
    }

    public void setSno(String sno) {
        this.sno = sno == null ? null : sno.trim();
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory == null ? null : dormitory.trim();
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

    public Date getCreatetime() {
        return createtime == null ? null : (Date) createtime.clone();
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime == null ? null : (Date) createtime.clone();
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }
}
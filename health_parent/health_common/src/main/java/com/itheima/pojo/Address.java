package com.itheima.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description TODO
 * @Author W12777
 * @Date 2021/8/22 15:38
 * @Version 1.0
 **/

public class Address implements Serializable {
    private Integer id;
    private String addressName;//地址id
    private Double longitude;//经度
    private Double latitude;//维度
    private String remark;//描述


    public Address(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}

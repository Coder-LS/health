package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Address;
import com.itheima.pojo.CheckItem;
import com.itheima.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    public void add(Order order);
    public List<Order> findByCondition(Order order);
    public Map findById4Detail(Integer id);
    public Integer findOrderCountByDate(String date);
    public Integer findOrderCountAfterDate(String date);
    public Integer findVisitsCountByDate(String date);
    public Integer findVisitsCountAfterDate(String date);
    public List<Map> findHotSetmeal();

    //查询所有预约列表
    public List<Order> findAll();

    public Page<Order> selectByCondition(String queryString);

    public void deleteById(Integer id);

    public Page<Address> selectAddressByCondition(String queryString);

    public void addAddress(@Param("addressName") String addressName, @Param("lat") String lat, @Param("lng") String lng);

    public void deleteAddressById(Integer id);

    List<Address> findAllAddress();
}

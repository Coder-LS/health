package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Address;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.Permission;

import java.util.List;
import java.util.Map;

public interface OrderService {
    public Result order(Map map) throws Exception;
    public Map findById(Integer id) throws Exception;

    public List<Order> findAll();

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public Result add(Map map, Integer[] setmealIds) throws Exception;

    public void deleteById(Integer id);

    public void deleteAddressById(Integer id);

    public PageResult addressPageQuery(QueryPageBean queryPageBean);

    public void addAddress(String addressName, String lat, String lng);

    List<Address> findAllAddress();
}

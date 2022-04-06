package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.Setmeal;
import com.itheima.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author W12777
 * @Date 2021/8/18 18:43
 * @Version 1.0
 **/
@RestController
@RequestMapping("/orderlist")
public class OrderListController {

    @Reference
    private OrderService orderService;


    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return orderService.pageQuery(queryPageBean);
    }


    //地址分页查询
    @RequestMapping("/findAddressPage")
    public PageResult findAddressPage(@RequestBody QueryPageBean queryPageBean){
        return orderService.addressPageQuery(queryPageBean);
    }


    //新增预约订单
    @RequestMapping("/add")
    public Result add(@RequestBody Map map, Integer[] setmealIds){
        Result result = null;
        try{
            result = orderService.add(map,setmealIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ORDER_FAIL);
        }
        //return new Result(true,MessageConstant.ORDER_SUCCESS);
        return result;
    }


    //删除订单预约
    //@PreAuthorize(value = "hasAuthority('CHECKITEM_DELETE')")//权限校验
    @RequestMapping("/delete")
    public Result delete(Integer id){
        try{
            orderService.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.ORDER_CANCEL_FAIL);
        }
        return  new Result(true, MessageConstant.ORDER_CANCEL_SUCCESS);
    }


    //删除地址
    //@PreAuthorize(value = "hasAuthority('CHECKITEM_DELETE')")//权限校验
    @RequestMapping("/deleteAddressById")
    public Result deleteAddressById(Integer id){
        try{
            orderService.deleteAddressById(id);
        }catch (Exception e){
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.DELETE_ADDRESS_FAIL);
        }
        return  new Result(true, MessageConstant.DELETE_ADDRESS_SUCCESS);
    }

    /**
    //新增预约订单
    @RequestMapping("/addAddress")  addressName 中文乱码
    public Result addAddress(String addressName, String lat , String lng){
        try{
            orderService.addAddress(addressName,lat,lng);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_ADDRESS_FAIL);
        }
        return new Result(true,MessageConstant.ADD_ADDRESS_SUCCESS);
    }
    */

    //新增地址
    @RequestMapping("/addAddress")
    public Result addAddress(@RequestBody Map map){
        try{
            String addressName = map.get("addressName").toString();
            String lat = map.get("lat").toString();
            String lng = map.get("lng").toString();
            orderService.addAddress(addressName,lat,lng);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_ADDRESS_FAIL);
        }
        return new Result(true,MessageConstant.ADD_ADDRESS_SUCCESS);
    }



}

package com.itheima.controller;

import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.Setmeal;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户操作
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;

    //获得当前登录用户的用户名
    @RequestMapping("/getUsername")
    public Result getUsername(){
        //当Spring security完成认证后，会将当前用户信息保存到框架提供的上下文对象
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user);
        if(user != null){
            String username = user.getUsername();
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,username);
        }

        return new Result(false, MessageConstant.GET_USERNAME_FAIL);
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return userService.pageQuery(queryPageBean);
    }

    //新增用户
//    @PreAuthorize(value = "hasAuthority('CHECKITEM_ADD')")//权限校验
    @RequestMapping("/add")
    public Result add(@RequestBody User user){
        try{
            userService.add(user);
        }catch (Exception e){
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.ADD_USER_FAIL);
        }
        return  new Result(true, MessageConstant.ADD_USER_SUCCESS);
    }


    @RequestMapping("/addSinglePermission")
    public Result addSinglePermission(@RequestBody Permission permission){
        try{
            userService.addSinglePermission(permission);
        }catch (Exception e){
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.ADD_SINGLE_PERMISSION_FAIL);
        }
        return  new Result(true, MessageConstant.ADD_SINGLE_PERMISSION_SUCCESS);
    }


    //查询所有角色
    @RequestMapping("/findAllRole")
    public Result findAllRole(){
        try{
            List<Role> list = userService.findAllRole();
            return new Result(true,MessageConstant.GET_ALLROLE_SUCCESS,list);//查询成功
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ALLROLE_FAIL);//查询失败
        }
    }


    //新增预约订单
    @RequestMapping("/addRole")
    public Result addRole(String rowUserId, Integer[] roleIds){
        try{
            userService.addRole(rowUserId,roleIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_ROLE_FAIL);
        }
        return new Result(true,MessageConstant.ADD_ROLE_SUCCESS);
    }

    //查询角色权限
    @RequestMapping("/findAllPermission")
    public Result findAllPermission(){
        try{
            List<Permission> list = userService.findAllPermission();
            return new Result(true,MessageConstant.GET_ALLPERMISSIONS_SUCCESS,list);//查询成功
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ALLPERMISSIONS_FAIL);//查询失败
        }
    }

    //分页查询
    @RequestMapping("/findPermissionPage")
    public PageResult findPermissionPage(@RequestBody QueryPageBean queryPageBean){
        return userService.pagePermissionQuery(queryPageBean);
    }


    //新增预约订单
    @RequestMapping("/addPermission")
    public Result addPermission(String rowRoleId, Integer[] permisssionIds){
        try{
            userService.addPermission(rowRoleId,permisssionIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_PERMISSIONS_FAIL);
        }
        return new Result(true,MessageConstant.ADD_PERMISSIONS_SUCCESS);
    }
}

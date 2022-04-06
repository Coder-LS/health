package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    public User findByUsername(String username);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public void add(User user);

    public List<Role> findAllRole();

    public void addRole(String rowUserId, Integer[] roleIds);

    List<Permission> findAllPermission();

    void addPermission(String rowRoleId, Integer[] permisssionIds);

    PageResult pagePermissionQuery(QueryPageBean queryPageBean);

    void addSinglePermission(Permission permission);
}

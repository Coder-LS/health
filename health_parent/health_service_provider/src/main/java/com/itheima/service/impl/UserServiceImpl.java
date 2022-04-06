package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.*;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户服务
 */
@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;


    //根据用户名查询数据库获取用户信息和关联的角色信息，同时需要查询角色关联的权限信息
    @Override
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);//查询用户基本信息，不包含用户的角色
        if(user == null){
            return null;
        }
        Integer userId = user.getId();
        //根据用户ID查询对应的角色
        Set<Role> roles = roleDao.findByUserId(userId);
        for (Role role : roles) {
            Integer roleId = role.getId();
            //根据角色ID查询关联的权限
            Set<Permission> permissions = permissionDao.findByRoleId(roleId);
            role.setPermissions(permissions);//让角色关联权限
        }
        user.setRoles(roles);//让用户关联角色
        return user;
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> page = userDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public PageResult pagePermissionQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Permission> page = permissionDao.findPermissionByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }



    @Override
    public void add(User user) {
        BCryptPasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        String password = passwordencoder.encode(user.getPassword());
        user.setPassword(password);
        userDao.add(user);
    }

    @Override
    public void addSinglePermission(Permission permission) {
        permissionDao.add(permission);
    }

    @Override
    public List<Role> findAllRole() {
        return roleDao.findAllRole();
    }

    @Override
    public void addRole(String rowUserId, Integer[] roleIds) {
        for (Integer roleId : roleIds) {
            String roleId1 = roleId.toString();
            roleDao.addRole(rowUserId,roleId1);
        }
    }

    @Override
    public List<Permission> findAllPermission() {
        return permissionDao.findAllPermission();
    }

    @Override
    public void addPermission(String rowRoleId, Integer[] permisssionIds) {
        for (Integer permisssionId : permisssionIds) {
            String permisssionId1 = permisssionId.toString();
            permissionDao.addPermission(rowRoleId,permisssionId1);
        }
    }
}

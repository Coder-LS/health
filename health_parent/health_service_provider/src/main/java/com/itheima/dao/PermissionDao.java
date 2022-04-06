package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PermissionDao {

    public Set<Permission> findByRoleId(Integer roleId);

    List<Permission> findAllPermission();

    void addPermission(@Param("rowRoleId")String rowRoleId, @Param("permisssionId1") String permisssionId1);

    Page<Permission> findPermissionByCondition(String queryString);

    void add(Permission permission);
}

package com.itheima.dao;

import com.itheima.pojo.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface RoleDao {

    public List<Role> findAllRole();

    public Set<Role> findByUserId(Integer userId);

    public void addRole(@Param("rowUserId") String rowUserId,@Param("roleId1") String roleId1);
}

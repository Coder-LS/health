package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Permission;
import com.itheima.pojo.User;

public interface UserDao {
    public User findByUsername(String username);

    public Page<CheckGroup> findByCondition(String queryString);

    public void add(User user);

}

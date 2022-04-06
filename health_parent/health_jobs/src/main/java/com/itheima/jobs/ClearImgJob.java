package com.itheima.jobs;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 自定义Job，实现定时清理垃圾图片
 */
//@MapperScan(value = "redis.clients.jedis")
public class ClearImgJob {

//    困扰很久的bug
//    @Autowired //Could not autowire. No beans of 'JedisPool' type found.   Inspection info:Checks autowiring problems
    @Autowired
//    @Autowired
    private JedisPool jedisPool;//一直报错但程序正常运行   在resources的jobs中导入redis.xml
    public void clearImg(){
        //根据Redis中保存的两个set集合进行差值计算，获得垃圾图片名称集合
//        Redis Sdiff 命令返回第一个集合与其他集合之间的差异，也可以认为说第一个集合中独有的元素。不存在的集合 key 将视为空集。
//        差集的结果来自前面的 FIRST_KEY ,而不是后面的 OTHER_KEY1，也不是整个 FIRST_KEY OTHER_KEY1..OTHER_KEYN 的差集。
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if(set != null){
            for (String picName : set) {
                //删除七牛云服务器上的图片 (qiniuyun连接配置删除操作)
                QiniuUtils.deleteFileFromQiniu(picName);
                //从Redis集合中删除图片名称 (删除点击文件图片上传但是没有新增套餐的redis里的图片 不是数据库里的)
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);
                System.out.println("自定义任务执行，清理垃圾图片:" + picName);
            }
        }
    }
}
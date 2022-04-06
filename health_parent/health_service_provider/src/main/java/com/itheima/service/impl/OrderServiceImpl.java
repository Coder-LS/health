package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.OrderSettingDao;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.*;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author W12777
 * @Date 2021/6/25 8:52
 * @Version 1.0
 **/

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    //体检预约
    @Override
    public Result order(Map map) throws Exception {
        //1.检查用户所选择的预约日期是否已经提前进行了预约设置,如果没有则无法进行预约
        String orderDate = (String) map.get("orderDate"); //预约日期
        OrderSetting orderSetting= orderSettingDao.findByOrderDate(DateUtils.parseString2Date(orderDate));
        if (orderSetting == null){
            //指定日期没有进行预约设置,无法完成体检预约
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }

        //2.检查用户所选择的预约日期是否已经约满,如果已经约满则无法预约
        int number = orderSetting.getNumber(); //可预约人数
        int reservations = orderSetting.getReservations(); //已预约人数
        if (reservations >= number){
            //已经约满，无法预约
            return new Result(false,MessageConstant.ORDER_FULL);
        }

        //3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
        String telephone = (String) map.get("telephone");//获取用户页面输入的手机号
        //用手机号判断是不是会员
        Member member = memberDao.findByTelephone(telephone);
        if(member != null){
            //判断是否在重复预约
            Integer memberId = member.getId();//会员ID
            Date order_Date = DateUtils.parseString2Date(orderDate);//预约日期
            String setmealId = (String) map.get("setmealId");//套餐ID
            Order order = new Order(memberId, order_Date, Integer.parseInt(setmealId));
            //根据条件进行查询
            List<Order> list = orderDao.findByCondition(order);
            if(list != null && list.size() > 0){
                //说明用户在重复预约，无法完成再次预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }

        }else {
            //4、检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);//自动完成会员注册
        }

        //5、预约成功，更新当日的已预约人数
        Order order = new Order();
        order.setMemberId(member.getId());//设置会员ID
        order.setOrderDate(DateUtils.parseString2Date(orderDate));//预约日期
        order.setOrderType((String) map.get("orderType"));//预约类型
        order.setOrderStatus(Order.ORDERSTATUS_NO);//到诊状态  未到诊
        order.setSetmealId(Integer.parseInt((String) map.get("setmealId")));//套餐ID
        orderDao.add(order);

        orderSetting.setReservations(orderSetting.getReservations() + 1);//设置已预约人数+1
        orderSettingDao.editReservationsByOrderDate(orderSetting);

        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    //根据预约ID查询预约相关信息（体检人姓名、预约日期、套餐名称、预约类型）
    @Override
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findById4Detail(id);
        if(map != null){
            //处理日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }

    @Override
    public List<Order> findAll() {
        return orderDao.findAll();
    }


    @Override
    public PageResult addressPageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Address> page = orderDao.selectAddressByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void addAddress(String addressName, String lat, String lng) {

        orderDao.addAddress(addressName,lat,lng);
    }

    //分页查询
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Order> page = orderDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //新增订单信息，同时需要关联组
    @Override
    public Result add(Map map, Integer[] setmealIds) throws Exception{

        //1.检查用户所选择的预约日期是否已经提前进行了预约设置,如果没有则无法进行预约
        String orderDate = (String) map.get("orderDate"); //预约日期
        OrderSetting orderSetting= orderSettingDao.findByOrderDate(DateUtils.parseString2Date(orderDate));
        if (orderSetting == null){
            //指定日期没有进行预约设置,无法完成体检预约
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //2.检查用户所选择的预约日期是否已经约满,如果已经约满则无法预约
        int number = orderSetting.getNumber(); //可预约人数
        int reservations = orderSetting.getReservations(); //已预约人数
        if (reservations >= number){
            //已经约满，无法预约
            return new Result(false,MessageConstant.ORDER_FULL);
        }


        Order order = new Order();
        for (int i = 0; i < setmealIds.length; i++) {


            //3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
            String phoneNum = (String) map.get("phoneNum");//获取用户页面输入的手机号
            //用手机号判断是不是会员
            Member member = memberDao.findByTelephone(phoneNum);
            if(member != null){
                //判断是否在重复预约
                Integer memberId = member.getId();//会员ID
                Date order_Date = DateUtils.parseString2Date(orderDate);//预约日期

//                String setmealId = (String) map.get("setmealId");//套餐ID
                Integer setmealId = setmealIds[i];//套餐ID
                order = new Order(memberId, order_Date, setmealId);
                //根据条件进行查询
                List<Order> list = orderDao.findByCondition(order);
                if(list != null && list.size() > 0){
                    //说明用户在重复预约，无法完成再次预约
                    return new Result(false,MessageConstant.HAS_ORDERED);
                }

            }else {
                //4、检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
                member = new Member();
                member.setName((String) map.get("name"));
                member.setPhoneNumber(phoneNum);
                member.setIdCard((String) map.get("idCard"));
                member.setSex((String) map.get("sex"));
                member.setRegTime(new Date());
                memberDao.add(member);//自动完成会员注册
            }

            //5、预约成功，更新当日的已预约人数

            order.setMemberId(member.getId());//设置会员ID
            order.setOrderDate(DateUtils.parseString2Date(orderDate));//预约日期
            order.setOrderType(Order.ORDERTYPE_TELEPHONE);//预约类型
            order.setOrderStatus(Order.ORDERSTATUS_NO);//到诊状态  未到诊
            order.setSetmealId(setmealIds[i]);//套餐ID
            orderDao.add(order);

            orderSetting.setReservations(orderSetting.getReservations() + 1);//设置已预约人数+1
            orderSettingDao.editReservationsByOrderDate(orderSetting);

        }


        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    @Override
    public void deleteById(Integer id) {
//        //判断当前检查项是否已经关联到检查组
//        long count = orderDao.findCountByCheckItemId(id);
//        if(count > 0){
//            //当前检查项已经被关联到检查组，不允许删除
//            new RuntimeException();
//        }
        orderDao.deleteById(id);

    }

    @Override
    public void deleteAddressById(Integer id) {
//        //判断当前检查项是否已经关联到检查组
//        long count = orderDao.findCountByCheckItemId(id);
//        if(count > 0){
//            //当前检查项已经被关联到检查组，不允许删除
//            new RuntimeException();
//        }
        orderDao.deleteAddressById(id);
    }

    @Override
    public List<Address> findAllAddress() {
        return orderDao.findAllAddress();
    }
}

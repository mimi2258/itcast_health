package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.CheckGroupDao;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/*检查组服务*/
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService{
    @Autowired
    private CheckGroupDao checkGroupDao;
    //新增检查组,同时关联检查项
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1.新增检查组,操作t_checkgroup表
        checkGroupDao.add(checkGroup);
        //2.设置查询组和查询项多对多关联关系,操作t_checkgroup_checkitem表
        Integer checkGroupId = checkGroup.getId();
        //检查id
        if (checkitemIds!=null&&checkitemIds.length>0){
            for (Integer checkitemId : checkitemIds) {
                Map<String,Integer> map=new HashMap<String, Integer>();
                map.put("checkgroupId",checkGroupId);
                map.put("checkitemId",checkitemId);
                //向中间关系表插入数据
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}

package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
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
        this.setCheckGroupAndChectItem(checkGroupId,checkitemIds);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //基于分页助手
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> page=checkGroupDao.selectByCondition(queryString);
        long total = page.getTotal();
        List<CheckGroup> rows = page.getResult();

        return new PageResult(total,rows);
    }

    //根据id查询检查组
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    //根据id查询检查项和检查组关联
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    //编辑检查组信息同时需要关联检查项
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1.修改检查组基本信息t_checkgroup
        checkGroupDao.edit(checkGroup);
        //2.先清理关联关系t_checkgroup_checkitem
        checkGroupDao.deleteAssoication(checkGroup.getId());
        //3.再进行编辑
        Integer checkGroupId = checkGroup.getId();
        this.setCheckGroupAndChectItem(checkGroupId,checkitemIds);
    }

    //通过id删除检查组
    public void delete(Integer id) {
        //1.首先判断检查组里有无检查项
        Integer count=checkGroupDao.findSetmealByCheckGroupId(id);
        //2.然后再执行删除
        if (count>0){
             new RuntimeException();
        }else {
            checkGroupDao.deleteAssoication(id);
            checkGroupDao.deleteCheckGroupById(id);
        }
    }

    //查询所有
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    //建立检查组与检查项多对多关系
    public void setCheckGroupAndChectItem(Integer checkGroupId,Integer[] checkitemIds){
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

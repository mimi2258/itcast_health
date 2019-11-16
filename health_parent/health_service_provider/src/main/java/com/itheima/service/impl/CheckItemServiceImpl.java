package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*检查项服务*/
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService{
    //注入dao对象
    @Autowired
    private CheckItemDao checkItemDao;

    //添加方法
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    //检查项分页方法
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //基于mybatis提供的分页助手
        PageHelper.startPage(currentPage,pageSize);//基于线程绑定
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        long total = page.getTotal();
        List<CheckItem> rows = page.getResult();
        return new PageResult(total,rows);
    }

    //根据id删除检查项
    public void deleteById(Integer id) {
        //首先判断当前检查项是否已经关联检查组
        long count = checkItemDao.findCountByCheckItemId(id);
        if (count>0){
            //当前检查项已经被关联到检查组了
            new RuntimeException();
        }
        checkItemDao.deleteById(id);
    }

    //编辑检查项
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    //通过id查询对应的检查项
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    //查询所有检查项
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}

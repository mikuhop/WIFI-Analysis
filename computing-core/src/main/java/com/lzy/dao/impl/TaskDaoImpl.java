package com.lzy.dao.impl;

import com.lzy.dao.TaskDao;
import com.lzy.bean.TaskBean;
import com.lzy.conf.MybatisSqlSession;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务信息数据库接口实现
 * Created by Liu Zi Yang on 2017/6/24 18:22.
 * E-mail address is kobeliuziyang@qq.com
 * Copyright © 2017 Liuziyang. All Rights Reserved.
 *
 * @author Liuziyang
 */
public class TaskDaoImpl implements TaskDao {
    // 得到log记录器
    private static final Logger logger = Logger.getLogger(TaskDaoImpl.class);

    @Override
    public int getTaskCount() {
        SqlSession sqlSession = MybatisSqlSession.getSqlSession();
        int count = 0;

        try {
            TaskDao taskDao = sqlSession.getMapper(TaskDao.class);
            count = taskDao.getTaskCount();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getStackTrace());
        } finally {
            sqlSession.close();
        }

        return count;
    }

    @Override
    public List<TaskBean> getTaskInfo() {
        SqlSession sqlSession = MybatisSqlSession.getSqlSession();
        List<TaskBean> taskBeanList = new ArrayList<>();

        try {
            TaskDao taskDao = sqlSession.getMapper(TaskDao.class);
            taskBeanList = taskDao.getTaskInfo();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getStackTrace());
        } finally {
            sqlSession.close();
        }
        return taskBeanList;
    }

    @Override
    public TaskBean getTaskById(Long id) {
        SqlSession sqlSession = MybatisSqlSession.getSqlSession();
        TaskBean taskBean = new TaskBean();

        try {
            TaskDao taskDao = sqlSession.getMapper(TaskDao.class);
            taskBean = taskDao.getTaskById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getStackTrace());
        } finally {
            sqlSession.close();
        }

        return taskBean;
    }

    @Override
    public void addTask(TaskBean taskBean) {
        SqlSession sqlSession = MybatisSqlSession.getSqlSession();

        try {
            TaskDao taskDao = sqlSession.getMapper(TaskDao.class);
            taskDao.addTask(taskBean);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getStackTrace());
        } finally {
            sqlSession.close();
        }
    }
}
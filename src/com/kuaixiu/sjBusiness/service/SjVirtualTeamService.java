package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.SjVirtualTeamMapper;
import com.kuaixiu.sjBusiness.entity.SjVirtualTeam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SjVirtualTeam Service
 * @CreateDate: 2019-05-26 下午06:00:35
 * @version: V 1.0
 */
@Service("sjVirtualTeamService")
public class SjVirtualTeamService extends BaseService<SjVirtualTeam> {
    private static final Logger log= Logger.getLogger(SjVirtualTeamService.class);

    @Autowired
    private SjVirtualTeamMapper<SjVirtualTeam> mapper;


    public SjVirtualTeamMapper<SjVirtualTeam> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}
package com.system.basic.dict.controller;

import com.common.base.controller.BaseController;
import com.system.basic.dict.entity.Dict;
import com.system.basic.dict.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dict Controller
 *
 * @CreateDate: 2016-09-26 下午11:12:03
 * @version: V 1.0
 */
@Controller
public class DictController extends BaseController {

    @Autowired
    private DictService dictService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dict/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="dict/list";
        return new ModelAndView(returnView);
    }
}

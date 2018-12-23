package com.system.basic.sequence.controller;

import com.common.base.controller.BaseController;
import com.system.basic.sequence.entity.Sequence;
import com.system.basic.sequence.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sequence Controller
 *
 * @CreateDate: 2016-09-03 下午11:14:22
 * @version: V 1.0
 */
@Controller
public class SequenceController extends BaseController {

    @Autowired
    private SequenceService sequenceService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sequence/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="sequence/list";
        return new ModelAndView(returnView);
    }
}

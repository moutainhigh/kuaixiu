package com.kuaixiu.nbTelecomSJ.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;
import com.kuaixiu.nbTelecomSJ.service.NBManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * NBManager Controller
 *
 * @CreateDate: 2019-02-22 下午06:34:26
 * @version: V 1.0
 */
@Controller
public class NBManagerController extends BaseController {

    @Autowired
    private NBManagerService nBManagerService;

}

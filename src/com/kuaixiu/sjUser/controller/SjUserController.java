package com.kuaixiu.sjUser.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.sjUser.service.SjUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * User Controller
 *
 * @CreateDate: 2019-05-06 上午10:19:30
 * @version: V 1.0
 */
@Controller
public class SjUserController extends BaseController {

    @Autowired
    private SjUserService userService;

}

package com.kuaixiu.sjBusiness.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.sjBusiness.service.OrderCompanyPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * OrderCompanyPicture Controller
 *
 * @CreateDate: 2019-05-06 上午10:43:53
 * @version: V 1.0
 */
@Controller
public class OrderCompanyPictureController extends BaseController {

    @Autowired
    private OrderCompanyPictureService orderCompanyPictureService;

}

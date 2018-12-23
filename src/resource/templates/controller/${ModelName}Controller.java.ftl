package ${ModelPath}.${ProjectName}.controller;

import com.common.base.controller.BaseController;
import ${ModelPath}.${ProjectName}.entity.${ModelName};
import ${ModelPath}.${ProjectName}.service.${ModelName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ${ModelName} Controller
 *
 * @CreateDate: ${CreateDate}
 * @version: V 1.0
 */
@Controller
public class ${ModelName}Controller extends BaseController {

    @Autowired
    private ${ModelName}Service ${ModelName?uncap_first}Service;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/${ModelName?uncap_first}/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="${ModelName?uncap_first}/list";
        return new ModelAndView(returnView);
    }
}

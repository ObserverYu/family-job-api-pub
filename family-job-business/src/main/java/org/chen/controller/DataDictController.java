package org.chen.controller;

import org.chen.domain.result.TemplateIdResult;
import org.chen.framework.annotion.LoggingFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.timer.UpdateTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 字典 前端控制器
 * </p>
 *
 * @author YuChen
 * @since 2020-12-18
 */
@RestController
@RequestMapping("/data-dict")
public class DataDictController {
    @Autowired
    private UpdateTimer updateTimer;

    @GetMapping("/getTemplateId")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    public TemplateIdResult getTemplateId(){
        String templateId = updateTimer.getTemplateId();
        TemplateIdResult result = new TemplateIdResult();
        result.setMeiritixing(templateId);
        return result;
    }

}

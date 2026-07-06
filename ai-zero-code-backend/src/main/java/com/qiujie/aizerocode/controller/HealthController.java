package com.qiujie.aizerocode.controller;


import com.qiujie.aizerocode.common.BaseResponse;
import com.qiujie.aizerocode.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/check")
    public BaseResponse<String> check() {
        return ResultUtils.success("ok");
    }
}

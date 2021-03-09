package com.futurewei.alcor.networkconfiguration.controller;

import com.futurewei.alcor.networkconfiguration.service.DpmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan(value = "com.futurewei.alcor.common.stats")
public class NcmController {

    @Autowired
    private DpmService dpmService;

}

package cn.dreamchan.controller;


import cn.dreamchan.common.DefContants;
import cn.dreamchan.common.Result;
import cn.dreamchan.config.MqttConfig;
import cn.dreamchan.utils.JacksonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DreamChan
 * @Description: 提供http请求 调用发送mqtt消息
 * @date 12/4/2019
 */
@Slf4j
@RestController
@Api(tags = "调用API发送mqtt消息")
@RequestMapping("/v1/provide")
public class MqttProvideController {

    @Autowired
    private MqttConfig.MqttProducer mqttProducer;

    @ApiOperation(value = "发送指定数据", notes = "发送指定数据")
    @GetMapping(value = "/device/syncdata")
    public Result<?> addOwner(@RequestParam String params) {
        if (StringUtils.isEmpty(params)) {
            return Result.error("发送参数不能为空");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        mqttProducer.sendToMqtt(DefContants.PRODUCER_TEST_TOPIC , JacksonUtils.writeValue(map));
        return Result.ok();
    }
}

package cn.dreamchan.mqtt;

import cn.dreamchan.common.DefContants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author DreamChan
 * @Description: mqtt消息接收后的处理
 * @date 19/1/2020
 */
@Component
@Slf4j
public class MqttCallbackHandle {

    /**
     * 接收消息处理
     * @param topic
     * @param payload
     */
    public void handle(String topic, String payload){
        log.info("MqttCallbackHandle:" + topic + "---"+ payload);

        // 根据topic分别进行消息处理。
        if (topic.endsWith(DefContants.CONSUMER_SYS_CLIENTS_CONNECTED_TOPIC)){
            // 设备上线
            log.info("设备上线");

        } else if (topic.endsWith(DefContants.CONSUMER_SYS_CLIENTS_DISCONNECTED_TOPIC)){
            // 设备离线
            log.info("设备离线");

        } else if (topic.equals(DefContants.CONSUMER_TEST_TOPIC)){
            // 接收到自定义消息
            log.info("自定义定义消息:  " + payload);
        }
    }


}

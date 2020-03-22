package cn.dreamchan.config;

import cn.dreamchan.common.DefContants;
import cn.dreamchan.mqtt.MqttCallbackHandle;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.StringUtils;


/**
 * @author DreamChan
 * @Description: mqtt 配置
 * @date 19/1/2020
 */
@Slf4j
@Configuration
public class MqttConfig {

    @Autowired
    private MqttEntity mqttEntity;

    @Autowired
    private MqttCallbackHandle mqttCallbackHandle;

    /**
     * 订阅的bean名称
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
    /**
     * 发布的bean名称
     */
    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";


    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

    /**
     * mqtt 连接配置
     *
     * @return
     */
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        mqttConnectOptions.setCleanSession(true);
        // 设置连接的用户名
        mqttConnectOptions.setUserName(mqttEntity.getUsername());
        // 设置连接的密码
        mqttConnectOptions.setPassword(mqttEntity.getPassword().toCharArray());
        // 设置连接mqtt服务器地址
        mqttConnectOptions.setServerURIs(mqttEntity.getUrl().split(","));
        // 设置超时时间 单位为秒
        mqttConnectOptions.setConnectionTimeout(30);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
        mqttConnectOptions.setKeepAliveInterval(60);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
        mqttConnectOptions.setWill("willTopic", WILL_DATA, 2, false);
        return mqttConnectOptions;
    }

    /**
     * mqtt 客户端
     * @return
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory () {
        DefaultMqttPahoClientFactory defaultMqttPahoClientFactory = new DefaultMqttPahoClientFactory();
        defaultMqttPahoClientFactory.setConnectionOptions(getMqttConnectOptions());
        return defaultMqttPahoClientFactory;
    }

    /**
     * MQTT信息通道（生产者）
     * @return
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutBoundChannel(){
        return new DirectChannel();
    }

    /**
     * MQTT消息处理器（生产者）
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound(){
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                mqttEntity.getProducerClientId(),
                mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(mqttEntity.getProducerDefaultTopic());
        return messageHandler;
    }

    /**
     * 生产者 用于消息发送
     */
    @MessagingGateway(defaultRequestChannel = CHANNEL_NAME_OUT)
    public interface MqttProducer {
        //用于消息发送
        void sendToMqtt(String payload);

        // 指定topic进行消息发送
        void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

        // 指定topic qos 进行消息发送
        void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
    }


    /**
     * MQTT消息订阅绑定
     * @return
     */
    @Bean
    public MessageProducer inbound() {
        // 可以同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                        mqttEntity.getConsumerClientId(), mqttClientFactory(),
                        mqttEntity.getConsumerDefaultTopic().split(","));

        adapter.addTopic(DefContants.CONSUMER_TEST_TOPIC);
        adapter.addTopic(DefContants.CONSUMER_SYS_CLIENTS_TOPIC);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInBoundChannel());
        return adapter;
    }

    /**
     * MQTT信息通道（消费者）
     * @return
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInBoundChannel(){
        return new DirectChannel();
    }


    /**
     *  MQTT消息处理器（消费者）
     *  ServiceActivator注解表明当前方法用于处理MQTT消息，inputChannel参数指定了用于接收消息信息的channel
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    public MessageHandler handler() {
         return message -> {
             String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
             String payload = message.getPayload().toString();
             mqttCallbackHandle.handle(topic, payload);
         };
    }

}

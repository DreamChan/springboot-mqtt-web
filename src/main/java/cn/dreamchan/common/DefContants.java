package cn.dreamchan.common;

/**
 * @author DreamChan
 * @Description: 常量
 * @date 19/1/2020
 */
public interface DefContants {

    /**
     * mqtt消费者 topic  订阅系统主题
     */
    public static String CONSUMER_SYS_CLIENTS_TOPIC = "$SYS/brokers/emqx@127.0.0.1/clients/#";

    /**
     * 客户端 连接后缀
     */
    public static String CONSUMER_SYS_CLIENTS_CONNECTED_TOPIC = "/connected";

    /**
     * 客户端  断开连接后缀
     */
    public static String CONSUMER_SYS_CLIENTS_DISCONNECTED_TOPIC = "/disconnected";

    /**
     * 发送的topic
     */
    public static String PRODUCER_TEST_TOPIC = "producer_topic";

    /**
     * 订阅的topic
     */
    public static String CONSUMER_TEST_TOPIC = "comsumer_topic";
}

package com.kang.test.k8s.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author BigKang
 * @Date 2021/3/31 上午10:51
 * @Summarize RocketMQ配置类
 */
@Component
@ConfigurationProperties(prefix = "rocketmq")
@Getter
@Setter
@ToString
public class RocketMqConfig {

    /**
     * NameServer地址，与Broker中的NameServer一致
     */
    private String nameSrvAddr = "localhost:9876";

    /**
     * 默认生产者消费者组
     */
    private String defaultGroup = "default-botpy-group";

    /**
     * 默认生产者消费者Topic
     */
    private String defaultTopic = "default-botpy-topic";

    /**
     * 默认生产者消费者Namespace
     */
    private String defaultNamespace;

    /**
     * 生产者默认配置
     */
    private ProducerProperties producer = new ProducerProperties();

    /**
     * 消费者默认配置
     */
    private ConsumerProperties consumer = new ConsumerProperties();

    /**
     * 是否开启RocketMQ
     */
    private boolean enable = false;

    @Getter
    @Setter
    @ToString
    public static class ProducerProperties{
        /**
         * 生产者命名空间，不设置则不使用
         */
        private String namespace;

        /**
         * 生产者组，不设置则采用默认
         */
        private String group;

        /**
         * 生产者Topic，不设置则采用默认
         */
        private String topic;

        /**
         * 生产者标签，不设置则采用默认
         */
        private String tag = "botpy";

        /**
         * 生产者默认的Topic创建队列数
         */
        private int defaultTopicQueueNums = 4;

        /**
         * 消息最大长度 默认1024 * 1024 * 4(4M)
         */
        private int maxMessageSize = 1024 * 1024 * 4;

        /**
         * 压缩消息正文阈值，即大于4K的消息正文将默认压缩。
         */
        private int compressMsgBodyOverHowmuch  = 1024 * 4;

        /**
         * 发送消息超时时间
         */
        private int sendMsgTimeout = 3000;

        /**
         * 在同步模式下发送故障之前，在内部重试次数,这可能导致消息重复，需要开发人员来解决。
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * 在异步模式下发送故障之前，在内部重试次数,这可能导致消息重复，需要开发人员来解决。
         */
        private int retryTimesWhenSendAsyncFailed = 2;

    }

    @Getter
    @Setter
    @ToString
    public static class ConsumerProperties{

        /**
         * 消费者命名空间，不设置则不使用
         */
        private String namespace;

        /**
         * 消费者组，不设置则采用默认
         */
        private String group;

        /**
         * 消费者Topic，不设置则采用默认
         */
        private String topic;

        /**
         * 消费者标签表达式，不设置则采用，*（所有Tag）
         */
        private String tagExpression = "*";

        /**
         * 消费者最小线程数
         */
        private int consumeThreadMin = 20;

        /**
         * 消费者最大线程数
         */
        private int consumeThreadMax = 20;

        /**
         * 动态调整线程池数的阈值
         */
        private long adjustThreadPoolNumsThreshold = 100000;

        /**
         * 同时最大跨度偏移量，它对顺序消耗没有影响
         */
        private int consumeConcurrentlyMaxSpan = 2000;

        /**
         * 消息拉取间隔
         */
        private long pullInterval = 0;

        /**
         * 批量拉取数量
         */
        private int pullBatchSize = 32;

        /**
         * 批量消费数量最大值
         */
        private int consumeMessageBatchMaxSize = 1;

        /**
         * 队列级的流量控制阈值，每个消息队列将默认为大多数1000个消息缓存
         * 考虑到这一点 {@code pullBatchSize}, 瞬时值可能超过极限
         */
        private int pullThresholdForQueue = 1000;

        /**
         * 限制队列级别的缓存大小，每个消息队列将默认为大多数100个MIB消息缓存,
         * 考虑到这一点 {@code pullBatchSize}, 瞬时值可能超过极限
         * 只有消息体测量的消息的大小，所以它不准确
         */
        private int pullThresholdSizeForQueue = 100;

        /**
         * 消息的最大时间量可以阻止消费线程
         */
        private long consumeTimeout = 15;

        /**
         * 关闭消费者时等待邮件消耗的最长时间, 0表示没有等待
         */
        private long awaitTerminationMillisWhenShutdown = 0;

        /**
         * 消费者状态不正常的时候，采用定时拉取的拉取间隔（默认一秒）
         */
        private int pullTimeDelayMillsWhenException = 1000;

        /**
         * 消费者持久化Offset间隔
         */
        private int persistConsumerOffsetInterval = 1000 * 5;
    }

    /**
     * 获取生产者Topic
     * @return
     */
    public String getProducerTopic(){
        if (StringUtils.isNotEmpty(producer.topic)) {
            return producer.topic;
        }else {
            return defaultTopic;
        }
    }

    /**
     * 获取生产者Group
     * @return
     */
    public String getProducerGroup(){
        if (StringUtils.isNotEmpty(producer.group)) {
            return producer.group;
        }else {
            return defaultGroup;
        }
    }

    /**
     * 获取生产者Namespace
     * @return
     */
    public String getProducerNamespace(){
        if (StringUtils.isNotEmpty(producer.namespace)) {
            return producer.namespace;
        }else {
            return defaultNamespace;
        }
    }


    /**
     * 获取消费者Topic
     * @return
     */
    public String getConsumerTopic(){
        if (StringUtils.isNotEmpty(consumer.topic)) {
            return consumer.topic;
        }else {
            return defaultTopic;
        }
    }

    /**
     * 获取消费者Group
     * @return
     */
    public String getConsumerGroup(){
        if (StringUtils.isNotEmpty(consumer.group)) {
            return consumer.group;
        }else {
            return defaultGroup;
        }
    }

    /**
     * 获取消费者Namespace
     * @return
     */
    public String getConsumerNamespace(){
        if (StringUtils.isNotEmpty(consumer.namespace)) {
            return consumer.namespace;
        }else {
            return defaultNamespace;
        }
    }
}


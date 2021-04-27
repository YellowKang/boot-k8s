package com.kang.test.k8s.utils;

import com.kang.test.k8s.config.RocketMqConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * @Author BigKang
 * @Date 2021/3/31 下午2:32
 * @Summarize RocketMQ工具类
 */
@Component
@Getter
@Slf4j
public class RocketMqUtil {

    /**
     * 默认搜索Tag表达式
     */
    private static final String ALL_TAGS_EXPRESSION = "*";

    /**
     * RocketMQ配置文件
     */
    private final RocketMqConfig rocketMqProperties;

    /**
     * 默认消息生产者
     */
    private DefaultMQProducer defaultProducer;

    @Autowired
    public RocketMqUtil(RocketMqConfig rocketMqProperties) {
        this.rocketMqProperties = rocketMqProperties;
        // 设置Client日志
        System.setProperty(ClientLogger.CLIENT_LOG_USESLF4J,"true");
        // 根据配置决定是否初始化默认生产者
        if (rocketMqProperties.isEnable()) {
            // 根据配置获取默认生产者
            defaultProducer = getProducer();
            log.info("begin RocketMQ init default producer!!!");
            try {
                // 启动默认生产者
                defaultProducer.start();
            } catch (MQClientException e) {
                log.error("RocketMQ init default producer failure!!!");
                e.printStackTrace();
            }
            log.info("RocketMQ init default producer success!!!");
        } else {
            log.info("RocketMQ cancel initialization default producer!!!");

        }
    }

    /**
     * 获取消费者实例(重载)
     *
     * @return
     */
    public DefaultMQPushConsumer getConsumer() {
        return getConsumer(
                rocketMqProperties.getConsumerTopic());
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param topic 订阅主题
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String topic) {
        return getConsumer(
                rocketMqProperties.getConsumerGroup(),
                topic);
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param group 消费者组
     * @param topic 订阅主题
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String group, String topic) {
        String tagExpression = null;
        // 设置默认Tag表达式
        if (StringUtils.isNotEmpty(rocketMqProperties.getConsumer().getTagExpression())) {
            tagExpression = rocketMqProperties.getConsumer().getTagExpression();
        }
        return getConsumer(
                group,
                topic,
                tagExpression);
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param group         消费者组
     * @param topic         订阅主题
     * @param tagExpression 标签表达式
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String group, String topic, String tagExpression) {
        return getConsumer(
                group,
                topic,
                tagExpression,
                rocketMqProperties.getConsumer().getPullBatchSize());
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param group         消费者组
     * @param topic         订阅主题
     * @param tagExpression 标签表达式
     * @param pullBatchSize 批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String group, String topic, String tagExpression, int pullBatchSize) {
        return getConsumer(
                rocketMqProperties.getConsumerNamespace(),
                group,
                topic,
                tagExpression,
                pullBatchSize);
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param namespace     消费者命名空间
     * @param group         消费者组
     * @param topic         订阅主题
     * @param tagExpression 标签表达式
     * @param pullBatchSize 批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String namespace, String group, String topic, String tagExpression, int pullBatchSize) {
        return getConsumer(
                namespace,
                group,
                topic,
                tagExpression,
                rocketMqProperties.getConsumer().getConsumeThreadMin(),
                rocketMqProperties.getConsumer().getConsumeThreadMax(),
                pullBatchSize);
    }


    /**
     * 获取消费者实例(重载)
     *
     * @param namespace        消费者命名空间
     * @param group            消费者组
     * @param topic            订阅主题
     * @param tagExpression    标签表达式
     * @param consumeThreadMin 消费者最小的线程数
     * @param consumeThreadMax 消费者最大的线程数
     * @param pullBatchSize    批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String namespace, String group, String topic, String tagExpression, int consumeThreadMin, int consumeThreadMax, int pullBatchSize) {
        return getConsumer(
                namespace,
                group,
                topic,
                tagExpression,
                consumeThreadMin,
                consumeThreadMax,
                rocketMqProperties.getConsumer().getPullInterval(),
                pullBatchSize);
    }

    /**
     * 获取消费者实例
     *
     * @param namespace        消费者命名空间
     * @param group            消费者组
     * @param topic            订阅主题
     * @param tagExpression    标签表达式
     * @param consumeThreadMin 消费者最小的线程数
     * @param consumeThreadMax 消费者最大的线程数
     * @param pullInterval     拉取时间间隔
     * @param pullBatchSize    批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String namespace, String group, String topic, String tagExpression, int consumeThreadMin, int consumeThreadMax, long pullInterval, int pullBatchSize) {
        enableCheck();
        DefaultMQPushConsumer consumer = null;
        // 如果命名空间不为空才进行设置
        if (StringUtils.isNotEmpty(namespace)) {
            consumer = new DefaultMQPushConsumer(namespace, group);
        } else {
            consumer = new DefaultMQPushConsumer(group);
        }
        // 设置NameServer地址
        consumer.setNamesrvAddr(rocketMqProperties.getNameSrvAddr());
        // 设置消费者最小线程数
        consumer.setConsumeThreadMin(consumeThreadMin);
        // 设置消费者最大线程数
        consumer.setConsumeThreadMax(consumeThreadMax);
        // 设置拉取间隔
        consumer.setPullInterval(pullInterval);
        // 设置拉取批量数量
        consumer.setPullBatchSize(pullBatchSize);
        // Topic为空则先不订阅
        if (StringUtils.isNotEmpty(topic)) {
            try {
                // Tag表达式为空则订阅所有
                if (StringUtils.isNotEmpty(tagExpression)) {
                    consumer.subscribe(topic, tagExpression);
                } else {
                    consumer.subscribe(topic, ALL_TAGS_EXPRESSION);
                }
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }
        return consumer;
    }

    /**
     * 获取生产者实例
     *
     * @return
     */
    public DefaultMQProducer getProducer() {
        return getProducer(rocketMqProperties.getProducerGroup());
    }

    /**
     * 获取生产者实例
     *
     * @param group 生产者组
     * @return
     */
    public DefaultMQProducer getProducer(String group) {
        return getProducer(rocketMqProperties.getProducerNamespace(), group);
    }

    /**
     * 获取生产者实例
     *
     * @param namespace 生产者命名空间
     * @param group     生产者组
     * @return
     */
    public DefaultMQProducer getProducer(String namespace, String group) {
        enableCheck();
        DefaultMQProducer producer = null;
        // 如果命名空间不为空才进行设置
        if (StringUtils.isNotEmpty(namespace)) {
            producer = new DefaultMQProducer(namespace, group);
        } else {
            producer = new DefaultMQProducer(group);
        }
        // 设置NameServer地址
        producer.setNamesrvAddr(rocketMqProperties.getNameSrvAddr());
        // 设置Topic队列数量
        producer.setDefaultTopicQueueNums(rocketMqProperties.getProducer().getDefaultTopicQueueNums());
        // 设置消息最大长度
        producer.setMaxMessageSize(rocketMqProperties.getProducer().getMaxMessageSize());
        // 设置压缩消息正文阈值
        producer.setCompressMsgBodyOverHowmuch(rocketMqProperties.getProducer().getCompressMsgBodyOverHowmuch());
        // 设置消息发送超时时间
        producer.setSendMsgTimeout(rocketMqProperties.getProducer().getSendMsgTimeout());
        // 设置同步模式下发送故障，重试次数
        producer.setRetryTimesWhenSendFailed(rocketMqProperties.getProducer().getRetryTimesWhenSendFailed());
        // 设置异步模式下发送故障，重试次数
        producer.setRetryTimesWhenSendAsyncFailed(rocketMqProperties.getProducer().getRetryTimesWhenSendAsyncFailed());
        return producer;
    }

    /**
     * 发送消息（重载）
     * @param msg 消息
     * @return
     */
    public SendResult sendMsg(String msg) {
        return sendMsg(
                msg,
                rocketMqProperties.getProducerTopic());
    }

    /**
     * 发送消息（重载）
     * @param msg 消息
     * @param topic 主题
     * @return
     */
    public SendResult sendMsg(String msg, String topic) {
        return sendMsg(
                msg,
                topic,rocketMqProperties.getProducer().getTag());
    }

    /**
     * 发送消息
     * @param msg 消息
     * @param topic 主题
     * @param tag 标签
     * @return
     */
    public SendResult sendMsg(String msg, String topic, String tag) {
        // 检查消息
        Message message = strToMessage(msg,topic,tag);
        return sendMsg(message);
    }

    /**
     * 发送消息
     * @param msg
     */
    public SendResult sendMsg(Message msg) {
        enableCheck();
        SendResult result = null;
        try {
            result = defaultProducer.send(msg);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送异步消息
     *
     * @param msg
     * @param sendCallback
     */
    public void sendAsyncMsg(String msg, SendCallback sendCallback) {
        checkMessage(msg);
        Message message = strToMessage(msg);
        sendAsyncMsg(message, sendCallback);
    }

    /**
     * 发送异步消息
     *
     * @param msg
     * @param sendCallback
     */
    public void sendAsyncMsg(Message msg, SendCallback sendCallback) {
        enableCheck();
        try {
            defaultProducer.send(msg, sendCallback);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符串转Message（重载）
     * @param msg 消息字符串
     * @return
     */
    public Message strToMessage(String msg) {
        return strToMessage(
                msg,rocketMqProperties.getProducerTopic());
    }

    /**
     * 字符串转Message（重载）
     * @param msg 消息字符串
     * @param topic Topic主题
     * @return
     */
    public Message strToMessage(String msg, String topic) {
        return strToMessage(
                msg,
                topic,
                rocketMqProperties.getProducer().getTag());
    }

    /**
     * 字符串转Message
     * @param msg 消息字符串
     * @param topic Topic主题
     * @param tag Tag标签
     * @return
     */
    public Message strToMessage(String msg, String topic, String tag) {
        Message message = new Message(topic,tag, msg.getBytes());
        return message;
    }

    /**
     * 检查消息
     *
     * @param msg
     */
    private void checkMessage(String msg) {
        Assert.isTrue(StringUtils.isNotEmpty(msg), "RocketMQ send async message is null!!!");
    }

    /**
     * 是否启用检查
     */
    private void enableCheck() {
        Assert.isTrue(rocketMqProperties.isEnable(), "RocketMq is not enable,use ${rocketmq.enable}=true Open!!!");
    }
}


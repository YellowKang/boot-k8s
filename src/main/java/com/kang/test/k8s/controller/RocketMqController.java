package com.kang.test.k8s.controller;

import com.kang.test.k8s.utils.RocketMessageResp;
import com.kang.test.k8s.utils.RocketMqUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2021/4/1 上午11:35
 * @Summarize
 */
@RestController
@RequestMapping("/rocketmq")
@Slf4j
public class RocketMqController {

    private final RocketMqUtil rocketMqUtil;

    @Autowired
    public RocketMqController(RocketMqUtil rocketMqUtil) {
        this.rocketMqUtil = rocketMqUtil;
    }


    @GetMapping("consumer")
    public List<RocketMessageResp> consumer(
            String topic,
            String group,
            String tagExpression,
            Integer time) {
        if (StringUtils.isEmpty(topic)) {
            topic = rocketMqUtil.getRocketMqProperties().getConsumerTopic();
        }
        if (StringUtils.isEmpty(group)) {
            group = rocketMqUtil.getRocketMqProperties().getConsumerGroup();
        }
        if (StringUtils.isEmpty(tagExpression)) {
            tagExpression = rocketMqUtil.getRocketMqProperties().getConsumer().getTagExpression();
        }
        DefaultMQPushConsumer consumer = rocketMqUtil.getConsumer(group, topic, tagExpression);
        List<RocketMessageResp> list = new ArrayList<>();
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    RocketMessageResp messageResp = RocketMessageResp.builder()
                            .messageId(msg.getMsgId())
                            .tags(msg.getTags())
                            .queueId(msg.getQueueId())
                            .content(new String(msg.getBody()))
                            .topic(msg.getTopic())
                            .build();
                    list.add(messageResp);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.start();
            // 默认消费5秒钟数据
            if (time == null || time <= 0) {
                time = 5;
            }
            // 获取毫秒
            time *= 1000;
            // 睡眠线程
            Thread.sleep(time);
            // 关闭消费者
            consumer.shutdown();
        } catch (MQClientException | InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    @GetMapping("sendMsg")
    public SendResult sendMsg(String msg, String topic, String tag) {
        Assert.isTrue(StringUtils.isNotEmpty(msg), "Message Can not be empty !");
        if (StringUtils.isEmpty(topic)) {
            topic = rocketMqUtil.getRocketMqProperties().getProducerTopic();
        }
        if (StringUtils.isEmpty(tag)) {
            tag = rocketMqUtil.getRocketMqProperties().getProducer().getTag();
        }
        SendResult result = rocketMqUtil.sendMsg(msg, topic, tag);
        return result;
    }
}

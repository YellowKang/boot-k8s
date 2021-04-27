package com.kang.test.k8s.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author BigKang
 * @Date 2021/4/1 上午11:47
 * @Summarize Rocket消息返回实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RocketMessageResp {

    private String messageId;

    private String tags;

    private Integer queueId;

    private String content;

    private String topic;

}

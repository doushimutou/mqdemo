package com.ayt.mqdemo;/**
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @author ayt
 * @date 2018/7/2822:20
 */

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author ayt  on 20180728
 */
@Component
public class Consumer {

    /**
     * 消费者的组名
     */
    @Value("${apache.rocketmq.consumer.PushConsumer}")
    private String consumerGroup;

    /**
     * NameServer地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    @PostConstruct
    public void defaultMQPushConsumer() {
        //消费者的组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

        //指定NameServer地址，多个地址以 ; 隔开
        consumer.setNamesrvAddr(namesrvAddr);
        try {
            //订阅PushTopic下Tag为push的消息
            consumer.subscribe("Topic", "TagA");
            //设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
            //如果非第一次启动，那么按照上次消费的位置继续消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
                try {
                    for (MessageExt messageExt : list) {
                        //输出消息内容
                        System.out.println("messageExt: " + messageExt);

                        String messageBody = new String(messageExt.getBody(), "utf-8");
                        //输出消息内容
                        System.out.println("消费响应：Msg: " + messageExt.getMsgId() + ",msgBody: " + messageBody);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //稍后再试
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                //消费成功
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
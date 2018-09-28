package com.ayt.mqdemo;/**
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @author ayt
 * @date 2018/7/2822:20
 */

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * @author ayt  on 20180728
 */
@Component
public class Producer {
    /**
     * 生产者组名
     */
    @Value("${apache.rocketmq.producer.producerGroup}")
    private String producerGroup;
    /**
     * nameServer地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesevAddr;

    @PostConstruct
    public void rocketMQProducer(){
        //生产者的组名
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //指定nameserver地址
        producer.setNamesrvAddr(namesevAddr);
        try {
            producer.start();

            for (int i = 0; i < 100; i++){
                String messageBody = "消息内容" + i;
                String message = new String(messageBody.getBytes(), "utf-8");
                //构建消息
                Message msg=new Message("Topic","TagA","key"+i,message.getBytes());
                //发送消息
                SendResult result=producer.send(msg);
                System.out.println("发送响应MsgID"+result.getMsgId()+"，发送状态"+result.getSendStatus());

            }
        }catch (Exception e) {
                e.printStackTrace();
            }


    }








}

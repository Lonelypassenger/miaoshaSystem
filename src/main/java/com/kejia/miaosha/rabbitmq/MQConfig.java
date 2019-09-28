package com.kejia.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 20:39 2019/9/27
 * @MODIFY:
 */
@Configuration
public class MQConfig {

    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEADER_QUEUE = "header.queue";
    public static final String TOPIC_EXCHANGE = "topicExchage";
    public static final String FANOUT_EXCHANGE = "fanoutxchage";
    public static final String HEADERS_EXCHANGE = "headersExchage";

    /**
     * Direct模式 交换机Exchange
     * */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue miaoshaqueue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }

    /**
     * Topic模式 交换机Exchange，交换机模式不是直接将数据放入到了对列当中，而是将数据放入到一个交换机当中进行路由。
     * */
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }
    //
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }
    @Bean
    public TopicExchange topicExchage(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchage()).with("topic.key1");
    }
    //带#是指使用了通配符
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchage()).with("topic.#");
    }
    /**
     * Fanout模式 交换机Exchange
     * */
    @Bean
    public FanoutExchange fanoutExchage(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding FanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchage());
    }
    @Bean
    public Binding FanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchage());
    }
    /**
     * Header模式 交换机Exchange
     * 这种模式下绑定交换机和queue的方法是message里要有一个head部分，然后head里面又满足map中的key和value。这样才能在里面放东西。
     * */
    @Bean
    public HeadersExchange headersExchage(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headerQueue1() {
        return new Queue(HEADER_QUEUE, true);
    }
    @Bean
    public Binding headerBinding() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headerQueue1()).to(headersExchage()).whereAll(map).match();
    }



}

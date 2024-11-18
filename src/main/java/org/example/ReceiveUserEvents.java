package org.example;

import com.rabbitmq.client.*;

public class ReceiveUserEvents {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        if (argv.length < 1) {
            System.err.println("Usage: ReceiveUserEvents [binding_key]...");
            System.exit(1);
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();

        for (String bindingKey : argv) {
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
        }

        System.out.println(" [*] Waiting for user created events. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            String routingKey = delivery.getEnvelope().getRoutingKey();
            System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");

            if (routingKey.endsWith(".accounting")) {
                handleAccountingSystem(message);
            } else if (routingKey.endsWith(".email")) {
                handleEmailSystem(message);
            }

        };

        // Consume messages from the queue
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    private static void handleAccountingSystem(String message) {
        System.out.println("[Accounting System] Creating user in accounting: " + message);        
    }

    private static void handleEmailSystem(String message) {
        System.out.println("[Email System] Sending welcome email to: " + message);
    }

}

package org.example;

import com.rabbitmq.client.*;

public class EmitUserCreatedEvent {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {

        // Using the provided utility methods to get routing key and message
        String routingKey = getRouting(argv);  // Get routing key from the arguments
        String message = getMessage(argv);  // Get the message from the arguments

        // If no message or routing key was provided, we use default values
        System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

        // Set up the RabbitMQ connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare the exchange with topic type
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            // Publish the message to the exchange with the determined routing key
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        }
    }

    // Get routing key from the arguments, defaulting to "user.created"
    private static String getRouting(String[] strings) {
        if (strings.length < 1)
            return "user.created";  // Default routing key if no argument is provided
        return strings[0];  // First argument is the routing key
    }

    // Get message from the arguments, defaulting to "New user created"
    private static String getMessage(String[] strings) {
        if (strings.length < 2)
            return "New user created";  // Default message if no argument is provided
        return joinStrings(strings, " ", 1);  // Join the remaining arguments as the message
    }

    // Join strings into a single message, starting from a specific index
    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0) return "";
        if (length < startIndex) return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}

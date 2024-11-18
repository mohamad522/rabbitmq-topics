User Event System with RabbitMQ (Topic-based Messaging)
=======================================================

This project demonstrates how to use RabbitMQ for simulating user creation events in an e-commerce system. We utilize **Topic-based messaging** to trigger multiple actions upon a new user registration. The system sends an event when a new user is created, and this event is received by multiple subscribers. In our case, the **accounting system** creates a corresponding user and the **email system** sends a welcome email.

Project Structure
-----------------

1.  **EmitUserCreatedEvent.java** - A producer that publishes a user creation event to the RabbitMQ exchange.
2.  **ReceiveUserEvents.java** - A consumer that listens for user events and processes them (either for accounting or email systems).

* * * * *

RabbitMQ Architecture
---------------------

### Components:

-   **Exchange**: `topic_logs` (Topic-based exchange)
-   **Queues**:
    -   For accounting: `user.created.accounting` (binding key)
    -   For email: `user.created.email` (binding key)

### Routing Keys:

-   **Routing Key for Accounting System**: `user.created.accounting`
-   **Routing Key for Email System**: `user.created.email`

* * * * *

## Running the Project

### Prerequisites

Before running the project, ensure that you have the necessary tools and dependencies set up:

-   **Docker** to run RabbitMQ locally.
-   **IntelliJ IDEA** for creating and running the Maven project.

### 1\. Installing Docker and Running RabbitMQ

First, install Docker if it's not already installed:

`sudo apt install docker.io`

Then, use Docker to run RabbitMQ with the management plugin (this will allow you to access RabbitMQ's web management interface):

`docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4.0-management`

-   **5672** is the default RabbitMQ port for messaging.
-   **15672** is the port for RabbitMQ's web management UI.

Once RabbitMQ is running, you can access its management interface by navigating to `http://localhost:15672` in your web browser. The default login credentials are:

-   **Username**: `guest`
-   **Password**: `guest`

### 2\. Creating a Maven Project in IntelliJ IDEA

1.  Open **IntelliJ IDEA** and click on **Create New Project**.
2.  Choose **Maven** as the project type and click **Next**.

### 3\. Adding Dependencies

Ensure you have the following dependency in your `pom.xml` to connect to RabbitMQ using Java:

`<dependency>     <groupId>com.rabbitmq</groupId>     <artifactId>amqp-client</artifactId>     <version>5.14.0</version> </dependency>`

This dependency is required to work with RabbitMQ from your Java code.

### 4\. Running the Project with Parameters in IntelliJ IDEA

To run the project with the appropriate parameters in IntelliJ IDEA, follow these steps:

1.  Open the **Run/Debug Configurations** by clicking on the dropdown in the top-right corner and selecting **Edit Configurations**.
2.  Click the **+** icon and choose **Application**.
3.  In the **Name** field, give it a name like "Run User Events".
4.  In the **Main class** field, set it to the class you want to run, e.g., `org.example.ReceiveUserEvents`.
5.  In the **Program Arguments** field, provide the binding keys as parameters, for example:

    `user.created.accounting`

6.  Click **OK** to save the configuration.
7.  Now, click the **Run** button in IntelliJ to start the application.

* * * * *

Usage
-----

### 1\. EmitUserCreatedEvent (Producer)

The **EmitUserCreatedEvent** class is used to publish a user creation event to RabbitMQ. It requires a routing key and a message (username).

#### Command Line Usage

`java org.example.EmitUserCreatedEvent [routing_key] [message]`

-   **[routing_key]**: The routing key for the event (e.g., `user.created.accounting` or `user.created.email`).
-   **[message]**: The message contains the username (e.g., `"john"`).

#### Example:

`java org.example.EmitUserCreatedEvent user.created.accounting "john"`

This will send an event to the RabbitMQ exchange with the routing key `user.created.accounting` and the message `"john"`.

#### Default Values:

-   **Routing Key**: If no routing key is provided, it defaults to `user.created`.
-   **Message**: If no message is provided, it defaults to `"New user created"`.

* * * * *

### 2\. ReceiveUserEvents (Consumer)

The **ReceiveUserEvents** class is used to receive user creation events and simulate the actions for the accounting and email systems.

#### Command Line Usage

`java org.example.ReceiveUserEvents [binding_key]...`

-   **[binding_key]**: You can bind the consumer to different queues by specifying the routing keys. For example, binding to both `user.created.accounting` and `user.created.email`.

#### Example:

`java org.example.ReceiveUserEvents user.created.accounting`

This will bind the receiver to the accounting system queue and process the messages accordingly.

* * * * *

Test Cases
----------

### Test Case 1: Send and Receive Accounting Event

1.  Start the receiver and bind to `user.created.accounting`:

`java org.example.ReceiveUserEvents user.created.accounting`

2.  Send a message with the routing key `user.created.accounting`:

`java org.example.EmitUserCreatedEvent user.created.accounting "john"`

#### Expected Output on the receiver side:

`[*] Waiting for user created events. To exit press CTRL+C
[x] Received 'user.created.accounting':'john'
[Accounting System] Creating user in accounting: john`

* * * * *

### Test Case 2: Send and Receive Email Event

1.  Start the receiver and bind to `user.created.email`:

`java org.example.ReceiveUserEvents user.created.email`

2.  Send a message with the routing key `user.created.email`:

`java org.example.EmitUserCreatedEvent user.created.email "mike"`

#### Expected Output on the receiver side:

`[*] Waiting for user created events. To exit press CTRL+C
[x] Received 'user.created.email.*':'mike'
[Email System] Sending welcome email to: mike`

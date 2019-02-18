package io.opentracing.contrib.specialagent.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQ {
  static {
    try {
      createConnection();
    }
    catch (final JMSException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static Connection createConnection() throws JMSException {
    final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    final Connection connection = connectionFactory.createConnection();
    connection.start();
    return connection;
  }
}
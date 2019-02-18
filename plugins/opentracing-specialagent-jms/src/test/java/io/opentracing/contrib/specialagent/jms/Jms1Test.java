/* Copyright 2019 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opentracing.contrib.specialagent.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.management.j2ee.statistics.Stats;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import io.opentracing.contrib.specialagent.AgentRunner;
import io.opentracing.mock.MockTracer;

@RunWith(AgentRunner.class)
@AgentRunner.Config(bootstrap={ActiveMQConnectionFactory.class, ConnectionFactory.class, LoggerFactory.class, Stats.class, UTF8Buffer.class})
public class Jms1Test extends JmsTest {
  @Before
  public void before(final MockTracer tracer) throws JMSException {
    tracer.reset();
    connection = ActiveMQ.createConnection();
    connection.start();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  @After
  public void after() throws JMSException {
    session.close();
    connection.close();
  }
}
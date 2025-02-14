package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.camel.builder.RouteBuilder;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;



@ApplicationScoped
public class AWSCamelRoute extends RouteBuilder {
    

  @Produces 
  @Named("awsSNSClient")
  public SnsClient createSnsClient() {
    return SnsClient.create();
  }

  @Produces 
  @Named("awsSQSClient")
   public SqsClient createSqsClient() {
    return SqsClient.create();
  }
  
  @Override
  public void configure() throws Exception {
    
    from("direct:start")
      .routeId("SQS-autocreate")
      .to("aws2-sqs://test?amazonSQSClient=#awsSQSClient&delay=50&maxMessagesPerPoll=5&autoCreateQueue=true")

    from("direct:start")
      .routeId("SNS-Poll")
      .to("aws2-sns://test?amazonSNSClient=#awsSNSClient&subscribeSNStoSQS=true");

    from("aws2-sqs://test?amazonSQSClient=#awsSQSClient&delay=50&maxMessagesPerPoll=5")
      .routeId("SQS-client")
      .to("stream:out");

  }

}

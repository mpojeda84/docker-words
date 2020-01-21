package com.mapr.demos.words.producer;

import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class RecordsProducer {


  private String topic;

  private KafkaProducer<String, String> producer;


  private static final Logger log = LogManager.getLogger(RecordsProducer.class.getName());

  public RecordsProducer() {
    topic = System.getenv("MY_TOPIC");
    this.producer = createProducer();
  }

  public void produce() {

    Random random = new Random();
    while (true) {
      char[] word = new char[random.nextInt(8)
          + 3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
      for (int j = 0; j < word.length; j++) {
        word[j] = (char) ('a' + random.nextInt(26));
      }

      ProducerRecord<String, String> record = new ProducerRecord<>(topic, new String(word));
      producer.send(record);
      log.info("Word produced: " + word);
    }
  }

  private KafkaProducer<String, String> createProducer() {

    final Properties producerProps = new Properties();
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    producerProps
        .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");

    return new KafkaProducer<String, String>(producerProps);
  }


  public static void main(String[] args){
    new RecordsProducer().produce();
  }

}

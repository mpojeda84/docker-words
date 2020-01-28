package com.mapr.demos.words.consumer;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ojai.Document;
import org.ojai.store.Connection;
import org.ojai.store.DocumentMutation;
import org.ojai.store.DocumentStore;
import org.ojai.store.DriverManager;
import org.ojai.store.exceptions.DocumentExistsException;

public class RecordsConsumer {

  private String topic;
  private String consumerGroup;
  private String table;
  private KafkaConsumer<String, String> consumer;
  private Connection connection;
  private DocumentStore store;

  private static final Logger log = LogManager.getLogger(RecordsConsumer.class.getName());

  public RecordsConsumer() {
    topic = System.getenv("MY_TOPIC");
    consumerGroup = System.getenv("CONSUMER_GROUP");
    table = System.getenv("MY_TABLE");
    consumer = createConsumer(consumerGroup);
    connection = DriverManager.getConnection("ojai:mapr:");
    log.info("Connection to DB Oppened ...");
    store = connection.getStore(table);
    log.info("Table proxy created...");
  }

  private void consume() {
    consumer.subscribe(Arrays.asList(topic));

    try {
      while (true) {
        ConsumerRecords<String, String> records = this.consumer.poll(1000L);

        if (!records.isEmpty()) {
          Iterator iterator = records.iterator();

          while (iterator.hasNext()) {
            final ConsumerRecord<String, String> record = (ConsumerRecord) iterator.next();

            String string = (String) record.value();
            log.info("Consumed: " + string);

            saveToMapRDB(string);

          }
        }
      }
    } catch (Exception e) {
      log.error(e);
    } finally {
      if (this.consumer != null) {
        this.consumer.close();
      }
    }
  }

  private void saveToMapRDB(String word) {
    String id = String.valueOf(word.charAt(0));

    try {

      Document found = store.findById(id, "_id");
      if (found == null) {
        Document document = connection.newDocument();
        String[] array = new String[]{};
        document.setArray("words", array);
        document.setId(String.valueOf(word.charAt(0)));
        store.insert(document);
      }
    } catch (DocumentExistsException e) {
      log.error(e);
    }

    try{
      DocumentMutation mutation = connection.newMutation();
      mutation.append("words", Arrays.asList(word));
      store.update(id, mutation);
    } catch (Exception e) {
      log.error(e);
    }


  }

  public KafkaConsumer<String, String> createConsumer(String groupName) {

    String group =
        groupName == null ? String.valueOf(new Date().toInstant().toEpochMilli()) : groupName;

    Properties consumerProps = new Properties();
    consumerProps.put("group.id", group);
    consumerProps.put("key.deserializer", StringDeserializer.class.getName());
    consumerProps.put("value.deserializer", StringDeserializer.class.getName());
    consumerProps.put("auto.offset.reset", "earliest");
    consumerProps.put("enable.auto.commit", "true");
    consumerProps.put("max.poll.records", "10");
    return new KafkaConsumer(consumerProps);
  }

  public static void main(String[] args) {
    new RecordsConsumer().consume();
  }

}

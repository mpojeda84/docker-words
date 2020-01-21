FROM maprtech/pacc:6.1.0_6.0.0_centos7
RUN mkdir -p /usr/share/mapr-apps/
COPY target/words-producer-jar-with-dependencies.jar /usr/share/mapr-apps/words-producer-jar-with-dependencies.jar
CMD [ "java", "-jar", "/usr/share/mapr-apps/words-producer-jar-with-dependencies.jar"]
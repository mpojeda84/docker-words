Pre-requisites:
1. You need an unsecured Map-R cluster to run this example, psnode[90-92] is used here
2. Ensure stream topic was created: /user/mapr/words/streams/words:topic
3. Docker environment up and ready to run containers

To run the demo:

1. Clone this repo
2. Build the jar by executing `mvn package`
3. Build the docker image from the Dockerfile `docker build -f Dockerfile -t words-producer .`
4. Run the image passing:
  - CLDB nodes
  - Cluster name
  - MapR user
  - Topic to produce to
  
   ```docker run -it -e MAPR_CLUSTER=maikel.cluster -e MAPR_CLDB_HOSTS=psnode90,psnode91,psnode92 -e MAPR_CONTAINER_USER=mapr -e MY_TOPIC=/user/mapr/words/streams/words:topic words-producer```

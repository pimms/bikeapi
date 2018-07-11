FROM anapsix/alpine-java:8

WORKDIR /bike/
COPY target/bikeapi-1.0-SNAPSHOT.jar .

CMD java -jar bikeapi-1.0-SNAPSHOT.jar

EXPOSE 2001

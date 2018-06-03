FROM craigrueda/tcnative:latest
MAINTAINER craig@craigrueda.com

COPY target/gateway-sample.jar /

CMD ["java", "-Xmx1g", "-Xms1g", "-XX:+UseG1GC", "-jar", "/gateway-sample.jar"]
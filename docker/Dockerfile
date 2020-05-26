FROM nexus-docker-public-hosted.ossim.io/openjdk8:alpine-slim
ENV SPRING_CLOUD_CONFIG_ENABLED=true \
    SPRING_CLOUD_DISCOVERY_ENABLED=true
WORKDIR $HOME
EXPOSE 8080
COPY *.jar $HOME
CMD $HOME/run.sh

FROM chen.harbor.wonders.com/wild-image/skywalking-agent-java:8.0.1-spg
MAINTAINER  yuchen
WORKDIR /workspace
COPY *.jar /workspace/app.jar
COPY docker-java-project.sh /workspace/docker-java-project.sh
RUN chmod +x /workspace/docker-java-project.sh
#设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone
EXPOSE 19410
ENTRYPOINT ["/workspace/docker-java-project.sh"]
CMD ["-p","prod","-s","yes"]

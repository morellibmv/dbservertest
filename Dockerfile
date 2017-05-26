FROM java:8
VOLUME /tmp
ADD /build/libs/brunovargasdbserver.jar brunovargasdbserver.jar
RUN bash -c 'touch /brunovargasdbserver.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/brunovargasdbserver.jar"]

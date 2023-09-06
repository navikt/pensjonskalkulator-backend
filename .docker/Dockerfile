FROM navikt/java:17
COPY target/pensjonskalkulator-backend.jar /app/app.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

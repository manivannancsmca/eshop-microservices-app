# Stage 1: Use an official Confluent image to cleanly get the Avro files
FROM confluentinc/cp-kafka-connect:7.6.0 AS confluent-source
USER root
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-avro-converter:7.6.0

# Stage 2: Build your actual Debezium container
FROM debezium/connect:2.5
USER root

# Copy the exact Avro converter plugin folder from the Confluent stage
COPY --from=confluent-source /usr/share/confluent-hub-components/confluentinc-kafka-connect-avro-converter /kafka/connect/confluentinc-kafka-connect-avro-converter

USER kafka
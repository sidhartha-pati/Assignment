# Assignment
# Overview
This Flink application processes incoming messages from a Kafka topic, calculates the age of the person based on their date of birth, and routes the messages to different Kafka topics based on their age parity (even or odd). The processed messages are also persisted to a file system.

# Prerequisites
- Flink: Ensure Flink is installed and configured.
- Kafka: A running Kafka cluster with the input and output topics.
- Postgres: A postgres database to sink the published messages.
- Scala: Scala SDK installed.
- SBT: SBT build tool installed.

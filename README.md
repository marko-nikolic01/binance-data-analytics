# Binance - Big Data Analysis

This project focuses on real-time analysis of cryptocurrency market data from Binance. Leveraging Big Data technologies and a structured data pipeline based on the Medallion Architecture (Bronze, Silver, Gold layers), the project ingests raw streaming data, enhances its quality through transformation and enrichment, and prepares it for advanced analytics.

## Architecture
### Overview

The system processes real-time streaming data. Cryptocurrency market data from Binance is continuously ingested into message queue topics, representing the Raw Data Zone. The data is then transformed in real-time within the Transformation Zone before being stored in the Curated Zone for advanced analytics and decision-making.

From the Curated Zone, the cleaned and structured data is used to build analysis dashboards.

The entire system is containerized and managed by an orchestration tool.

<p align="center">
  <img alt="Untitled Diagram drawio (9)" src="https://github.com/user-attachments/assets/b823684e-0103-4905-81a0-d1e9663f7d38"/>
</p>

### Tools and Components

**Real-Time Data Source**: 
- [Binance - WebSocket Streams](https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams)


**Extraction Tool**:
- Python
- Apache NiFi  


**Data Zones**:
- **Raw Zone (Bronze)**: 
  - Apache Kafka

- **Transformation Zone (Silver)**: 
  - Apache Kafka

- **Curated Zone (Gold)**: 
  - MongoDB  


**Stream Processing**: 
- Apache Kafka
- Apache Kafka Streams


**Dashboards**: 
- Metabase


**Containerization**:
- Docker
- Docker Compose

<p align="center">
  <img alt="Untitled Diagram drawio (10)" src="https://github.com/user-attachments/assets/6dd54ac4-2c66-4c39-944f-185ef8a23ab0" />
</p>

## Analytical Questions
### Stream Processing Questions
  1. How have cryptocurrency prices evolved over time?
  2. What is the current percentage increase or decrease in cryptocurrency prices?
  3. How has cryptocurrency trading volume changed over time?
  4. What is the current percentage increase or decrease in trading volume?
  5. What is the price spread (difference between the highest and lowest prices) for each cryptocurrency?

## Stream Processing
### Data Source
Source: [Binance - WebSocket Streams](https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams).

The dataset consists of real-time cryptocurrency market data streamed directly from Binance’s official WebSocket API. The stream delivers continuous updates on trades, price changes, trading volume, and market activity for various cryptocurrency pairs.

Messages are delivered in JSON format and contain detailed information about each trade, including pricing, quantities, timestamps, trade identifiers.

<p align="center">
  <img alt="Binance_logo svg" height="80" src="https://github.com/user-attachments/assets/68c13db6-69dd-4ed4-9c6b-d543da54c4fb" />
</p>

### Extraction
Apache NiFi was used for the extraction process, which consists of the following phases:
1. A Python script establishes a real-time WebSocket connection to Binance and streams live cryptocurrency market data.
2. Apache NiFi ingests the streaming data directly from the Python script.
3. Split the data into smaller-sized files.
4. Rename the split files by adding handles for data and UUID.
5. The renamed files are published to a Kafka topic, representing the Raw Data Zone for downstream processing.

<p align="center">
  <img alt="Python-logo-notext svg" src="https://github.com/user-attachments/assets/1fde3009-f7f8-44b6-8ab4-6b29bcd35c77" />
</p>

<p align="center">
  <img alt="Kaggle_Logo" src="https://github.com/user-attachments/assets/1b8dcb5f-2b06-43c7-a61b-aea6fee4b0cc" />
</p>


<p align="center">
  <img alt="Screenshot_20250501_120437" src="https://github.com/user-attachments/assets/e0b2df4f-be1a-40b6-84ef-d24990505c1f" />
</p>

### Data Zones

The data pipeline follows the **Medallion Architecture**, which organizes the data into three distinct zones:
1. **Raw Data Zone (Bronze)**
2. **Transformation Zone (Silver)**
3. **Curated Zone (Gold)**

<p align="center">
  <img width="410" alt="Screenshot-2024-06-30-at-18 57 11" src="https://github.com/user-attachments/assets/c3a0c95f-4051-4d7d-b8d2-fda5294c9387" />
</p>

#### Raw Zone (Bronze)
The **Raw Zone** is implemented using Apache Kafka and stores incoming real-time data directly from the extraction layer without any modifications. It acts as a durable buffer for raw messages, ensuring the data is preserved in its original structure for downstream consumers.

<p align="center">
  <img src="https://github.com/user-attachments/assets/7ef19067-ab8b-4ec8-9cb0-0a52751a45d1" alt="Apache_Kafka_logo svg" />
</p>

#### Transformation Zone (Silver)
The **Transformation Zone** is implemented in Apache Kafka and contains real-time data that has been cleaned, enriched, and structured. This zone ensures that the data is in a consistent and analyzable format, enabling more complex processing and analytics.

<p align="center">
  <img src="https://github.com/user-attachments/assets/7ef19067-ab8b-4ec8-9cb0-0a52751a45d1" alt="Apache_Kafka_logo svg" />
</p>

#### Curated Zone (Gold)
The **Curated Zone (Gold)** is located in MongoDB and it is where the final, high-quality data is stored. This zone is typically optimized for reporting, dashboarding, and machine learning tasks. The data in the **Curated Zone** is fully cleaned, aggregated, and enriched, ensuring that it's in the most usable form for advanced analysis. This data is ready for decision-making processes, business intelligence tools, and further data science operations.

<p align="center">
  <img src="https://github.com/user-attachments/assets/6c1c3335-8cb5-456f-939a-2d8b65e825ad" alt="MongoDB_Fores-Green" />
</p>

### Data Processing
Data is processed using **Apache Kafka** and **Apache Kafka Streams** in two main phases, each corresponding to moving data from one zone to another within the Medallion Architecture: **Bronze**, **Silver**, and **Gold**.

<p align="center">
  <img src="https://github.com/user-attachments/assets/7ef19067-ab8b-4ec8-9cb0-0a52751a45d1" alt="Apache_Kafka_logo svg" />
</p>

#### Phase 1: Data Cleaning (Raw to Transformation) – **Bronze to Silver**  
In the first phase, the data in the **Raw Data Zone (Bronze)** is cleaned and transformed as it moves into the **Transformation Zone (Silver)**. The cleaning process is handled by the **`clean_binance_data_app`**, which consumes data from Kafka topics, performs necessary data cleansing, and then pushes the cleaned data to the **Transformation Zone (Silver)** in Kafka.

#### Phase 2: Data Transformation (Transformation to Curated) – **Silver to Gold**  
After the data is cleaned and structured in the **Transformation Zone (Silver)**, it is processed and enriched before being sent to the **Curated Zone (Gold)**. This phase includes several Kafka Streams applications, each responsible for a specific transformation or calculation:

1. **`calculate_moving_average`**: Computes the moving average of cryptocurrency prices over a specified time window. This helps in smoothing out price fluctuations and observing trends over time.

2. **`calculate_price_spread`**: Calculates the price spread (difference between high and low prices) for each cryptocurrency over a moving window, giving insight into market volatility.

3. **`calculate_trading_volume_by_hour_of_day`**: Calculates the trading volume for each cryptocurrency, helping to identify peak trading periods and activity patterns.

These jobs help transform and enrich the data, making it ready for visualization, reporting, and advanced analysis in the **Curated Zone (Gold)**.

### Dashboards

The transformed and enriched data in the **Curated Zone (Gold)** is presented and visualized using **Metabase**, an open-source business intelligence tool.

<p align="center">
  <img src="https://github.com/user-attachments/assets/62024e89-6f60-4ec7-b595-99dc22d3adad" width="200" alt="metabase-logo" />
</p>

A dedicated dashboard is created to visualize the key insights and metrics derived from the processed data.

<p align="center">
  <img src="https://github.com/user-attachments/assets/25c2ecbf-7ebc-4fd8-b06d-66b53d18daa5" alt="Screenshot_20250501_122659" />
</p>

## Containerization
Containerization of the application was achieved using Docker and Docker Compose.
<p align="center">
  <img src="https://github.com/user-attachments/assets/5b1fd6a3-22b3-464c-b01a-2fd035f74bbd" alt="Docker_logo" />
</p>

### Apache NiFi
<p align="center">
  <img alt="Kaggle_Logo" src="https://github.com/user-attachments/assets/1b8dcb5f-2b06-43c7-a61b-aea6fee4b0cc" />
</p>

<p align="center">
  <img alt="Python-logo-notext svg" src="https://github.com/user-attachments/assets/1fde3009-f7f8-44b6-8ab4-6b29bcd35c77" />
</p>

The NiFi setup consists of the following containers:

- **binance-connector** (`python:3.9-slim`):
  - Establishes and maintains a WebSocket connection to Binance.

- **niFi** (`apache/nifi:1.15.3`):
  - Manages data flows, data ingestion, and processing. Provides the main interface for creating and managing data pipelines.

- **nifi-registry** (`apache/nifi-registry:1.15.3`):
  - Stores and manages versioned NiFi data flows for tracking and version control of flow configurations.

### MongoDB
<p align="center">
  <img src="https://github.com/user-attachments/assets/6c1c3335-8cb5-456f-939a-2d8b65e825ad" alt="MongoDB_Fores-Green" />
</p>

The MongoDB setup consists of the following containers:

- **mongodb** (`mongo:8.0`):
  - A NoSQL database container that stores data for the Curated Zone (Gold).

- **mongo-express** (`mongo-express:1.0.2-20-alpine3.19`):
  - A web-based UI for interacting with MongoDB, allowing for easy database management and viewing data.

### Apache Kafka
<p align="center">
  <img src="https://github.com/user-attachments/assets/7ef19067-ab8b-4ec8-9cb0-0a52751a45d1" alt="Apache_Kafka_logo svg" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/7d740b5e-84a3-4151-9720-a2d9313cd2fa" alt="Apache_ZooKeeper_logo svg svg" />
</p>

The Kafka setup consists of the following containers:

- **zookeeper** (`zookeeper:3.8`):  
  - Provides coordination and configuration services required by Kafka. It manages broker metadata, leader election, and cluster state.

- **kafka** (`wurstmeister/kafka:2.13-2.8.1`):  
  - Acts as the core message broker. It handles the ingestion, storage, and distribution of real-time streaming data.

- **kafka-streams** (`openjdk:17`):  
  - A Kafka Streams application container. It consumes data from Kafka topics, performs real-time stream processing and produces the transformed results back into Kafka topics or into the database.

### Metabase
<p align="center">
  <img src="https://github.com/user-attachments/assets/62024e89-6f60-4ec7-b595-99dc22d3adad" width="200" alt="metabase-logo" />
</p>

The Metabase setup consists of the following containers:

- **metabase** (`metabase/metabase:v0.53.x`):
  - Provides an easy-to-use interface for data visualization and analytics.

FROM python:3.9-slim

WORKDIR /app

COPY ./binance_connector/requirements.txt /app/

RUN pip install --no-cache-dir -r requirements.txt

COPY ./binance_connector/scripts/ /app/

CMD ["python", "connect_to_binance.py"]

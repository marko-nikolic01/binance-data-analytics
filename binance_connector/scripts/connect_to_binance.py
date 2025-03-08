import websocket

def on_message(ws, message):
    with open("/data/binance_data.json", "a") as f:
        f.write(message + "\n")

def on_error(ws, error):
    print("Error:", error)

def on_close(ws, close_status_code, close_msg):
    print("Connection closed.")

def on_open(ws):
    print("WebSocket connection established.")

if __name__ == "__main__":
    streams = [
        "btcusdt@kline_1m",
        "ethusdt@kline_1m"
    ]

    url = "wss://stream.binance.com:9443/stream?streams=" + "/".join(streams)
    
    websocket.enableTrace(True)
    ws = websocket.WebSocketApp(url,
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)
    ws.on_open = on_open
    
    ws.run_forever()

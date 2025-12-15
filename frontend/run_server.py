import http.server
import socketserver
import os

PORT = 8000
DIRECTORY = "."

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

print(f"Iniciando servidor frontend en http://localhost:{PORT}")
print("Presiona Ctrl+C para detener.")

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    httpd.serve_forever()

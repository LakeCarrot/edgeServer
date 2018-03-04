docker stop ocr;
docker rm ocr;
docker run -p 50055:40051 --name ocr ruili92/ocr &


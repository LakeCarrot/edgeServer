docker stop speech;
docker rm speech;
docker run -p 50052:50052 --name speech ruili92/speech &


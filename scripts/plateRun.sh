docker stop plate;
docker rm plate;
docker run -p 50054:50052 -e "HOSTIP=`ip route get 8.8.8.8 | awk '{print $NF; exit}'`" --name plate bhu2017/platerec &

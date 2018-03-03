docker stop face;
docker rm face;
docker run -p 50053:50052 -e "HOSTIP=`ip route get 8.8.8.8 | awk '{print $NF; exit}'`" --name face bhu2017/facerec &

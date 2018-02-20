git add .
git commit -am "update."
git push

ssh dogtail@slave1 "pkill -f edgeServer; cd edgeServer; git pull; sh compile.sh; exit;"
ssh dogtail@slave2 "pkill -f edgeServer; cd edgeServer; git pull; sh compile.sh; exit;"
ssh dogtail@slave3 "pkill -f edgeServer; cd edgeServer; git pull; sh compile.sh; exit;"
ssh dogtail@master "pkill -f edgeServer; cd edgeServer; git pull; sh compile.sh; exit;"

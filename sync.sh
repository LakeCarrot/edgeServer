ssh dogtail@slave1 " pkill -f edgeServer; cd edgeServer; rm -rf src; exit;"
scp -r src dogtail@slave1:~/edgeServer/;
#$ssh dogtail@slave1 "cd edgeServer; sh compile.sh;"

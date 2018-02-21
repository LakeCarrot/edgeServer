git add .
git commit -am "update."
git push

ssh dogtail@slave1 "cd edgeServer; pkill -f edgeServer; git pull; sh compile.sh; exit;"

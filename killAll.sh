ssh dogtail@slave1 'docker stop $(docker ps -aq); docker rm $(docker ps -aq); exit;'
ssh dogtail@slave2 'docker stop $(docker ps -aq); docker rm $(docker ps -aq); exit;'
ssh dogtail@slave3 'docker stop $(docker ps -aq); docker rm $(docker ps -aq); exit;'
ssh dogtail@master 'docker stop $(docker ps -aq); docker rm $(docker ps -aq); exit;'
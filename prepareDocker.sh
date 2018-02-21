ssh dogtail@slave1 'docker pull ruili92/speech; sh speechRun.sh; exit;'
ssh dogtail@slave2 'docker pull ruili92/speech; sh speechRun.sh; exit;'
ssh dogtail@slave3 'docker pull ruili92/speech; sh speechRun.sh; exit;'
ssh dogtail@master 'docker pull ruili92/speech; sh speechRun.sh; exit;'

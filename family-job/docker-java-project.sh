#!/bin/bash
while getopts p:h:g:s:d OPT; do
 case ${OPT} in
  p) profile=${OPTARG}
    ;;
  h) heap=${OPTARG}
    ;;
  g) gc=${OPTARG}
    ;;
  s) skywalking=${OPTARG}
    ;;
  d) skywalkingdir=${OPTARG}
    ;;
  \?)
    printf "[Usage] `date '+%F %T'` -p <PROFILE> -h <HEAP> -g <
GC> -s <SKYWALKING> \n" >&2
    exit 1
 esac
done

echo '-p profile:'${profile}
echo '-h heap:'${heap}
echo '-g gc:'${gc}
echo '-s skywalking':${skywalking}


#配置文件
if [ x${profile} != x ]
then
    echo "使用自定配置文件${profile}"
else
    profile="prod"
    echo "使用默认配置文件prod"
fi

#堆大小
if [ x${heap} != x ]
then
   echo "使用自定堆大小${heap}"
else
   heap="512"
   echo "使用默认堆大小 512m"
fi

#使用其他的GC
if [ x${gc} != x ]
then
    echo "使用自定义GC策略 ${gc}"
else
    gc="+PrintGC"
    echo "使用默认GC策略 +PrintGC"
fi
JAVA_OPT="-Xms${heap}M -Xmx${heap}M -Dspring.profiles.active=${profile} -XX:${gc}"
#开启skywalking链路追踪
if [ x${skywalking} != x ]
then
	if [ x${skywalkingdir} != x ]
	then
		JAVA_OPT="-Xms${heap}M -Xmx${heap}M -Dspring.profiles.active=${profile} -XX:${gc} -javaagent:${skywalking}"
		echo "开启skywalking链路追踪,自定义jar目录: ${skywalking}"
	else
		JAVA_OPT="-Xms${heap}M -Xmx${heap}M -Dspring.profiles.active=${profile} -XX:${gc} -javaagent:/workspace/skywalking/agent/skywalking-agent.jar"
		echo "开启skywalking链路追踪,默认jar目录: /workspace/skywalking/agent/skywalking-agent.jar"
	fi
else
    echo "未开启skywalking链路追踪"
fi
echo "最终参数:${JAVA_OPT}"
echo "执行命令:exec java ${JAVA_OPT} -jar /workspace/app.jar 2>&1 < /dev/null"
exec java ${JAVA_OPT} -jar /workspace/app.jar 2>&1 < /dev/null
#!/bin/bash
# maven $workspace $jarname
# ${harborPro} ${projectName} ${docker_path} ${jarName}

set -e
harbor_namespace=$1
projectName=$2 
kubernetes_namespace=$3
profile=$4

# harbor认证用户
username=admin
password=Harbor12345

# harbor地址以及tag
tag=$(date +%s)
harbor_server=chen.harbor.wonders.com
taget_image=${projectName}:${tag}
#${BUILD_NUMBER}
echo ${taget_image}

# 登录docker
/usr/bin/docker login ${harbor_server} -u ${username} -p ${password}

# 生成镜像并推送到harbor,最后删除本地镜像
echo "The name of image is ${harbor_server}/${harbor_namespace}/${projectName}:${tag}"
/usr/bin/docker build -t ${harbor_server}/${harbor_namespace}/${projectName}:${tag} .
#sudo docker tag ${taget_image} ${harbor_server}/${harbor_namespace}/${projectName}
/usr/bin/docker push ${harbor_server}/${harbor_namespace}/${projectName}:${tag}
/usr/bin/docker rmi -f ${harbor_server}/${harbor_namespace}/${projectName}:${tag}
echo "kubectl use: kubectl set image deployment/${projectName}-${profile} *=${harbor_server}/${harbor_namespace}/${projectName}:${tag} -n ${kubernetes_namespace}"
/usr/bin/kubectl set image deployment/${projectName}-${profile} *=${harbor_server}/${harbor_namespace}/${projectName}:${tag} -n ${kubernetes_namespace}
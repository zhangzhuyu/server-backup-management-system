#基础镜像
FROM  192.168.2.78:5000/library/centos-jdk:1.8.275
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list

#飞腾arm芯片qilin系统认证
#FROM  registry.ly-sky.com:5000/library/open-jdk:1.8-arm

#作者
MAINTAINER Gaotao gaotao@ly-sky.com

#执行命令，主要用来安装相关的软件
#添加文件
ADD target/ly-ts-deploy-backup-svc-0.0.1-SNAPSHOT.jar /usr/local/ly-ts-deploy-backup-svc.jar
ADD src/conf /conf
ADD files/apache-maven-3.9.0.tar.gz /
ADD files/dependency-check-8.0.2-release.zip /
ADD files/data.tar.gz /
ADD target/ly-ts-deploy-backup-svc-0.0.1-SNAPSHOT.jar /a/ly-ts-deploy-backup-svc-0.0.1-SNAPSHOT.jar
ADD pom.xml /a/pom.xml
RUN unzip dependency-check-8.0.2-release.zip
#环境变量设置
#ENV #开放端口
EXPOSE 50016
#启动时执行的命令
#CMD ["/bin/bash"]
#启动时执行的命令
CMD ["java","-Dfile.encoding=UTF8", "-Duser.timezone=GMT+08", "-jar", "/usr/local/ly-ts-deploy-backup-svc.jar", "--spring.config.location=/conf/ly-ts-deploy-backup-svc-prod.yml"]
#解决部分场景连接数据库被重置的现象
#ENTRYPOINT ["java","-Dfile.encoding=UTF8", "-Duser.timezone=GMT+08", "-jar","-Djava.security.egd=file:/dev/./urandom","-Xms512m", "-Xmx4096m", "-XX:PermSize=256M", "-XX:MaxPermSize=256M","/usr/local/ly-ts-deploy-backup-svc.jar","--spring.config.location==/data/java/config/ly-ts-deploy-backup-svc-prod.yml"]


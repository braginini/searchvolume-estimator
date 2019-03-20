# Use openjdk as base image. To make your builds reproducible, make
# sure you lock down to a specific version, not to `latest`!
# See https://github.com/docker-library/openjdk for a list of version numbers.
FROM openjdk:8u131-jdk-alpine

RUN apk update && apk add ca-certificates bash curl tzdata jq && update-ca-certificates && cp -vf /usr/share/zoneinfo/Europe/Berlin /etc/localtime && echo "Europe/Berlin" | tee /etc/timezone

#################################################
# Service related environment variables
#################################################

ENV SERVICE_CONFIG_PATH /etc/estimator
ENV SERVICE_LIB_PATH /opt/estimator
ENV SERVICE_LOG_PATH /var/log/estimator
ENV SERVICE_MAIN_CLASS "missing.main.ClassName"

#################################################
# Add scripts
#################################################

COPY *.sh ${SERVICE_LIB_PATH}/
RUN chmod +x ${SERVICE_LIB_PATH}/*.sh

#################################################
# Create folders
#################################################

RUN mkdir ${SERVICE_CONFIG_PATH}
RUN mkdir -p ${SERVICE_LOG_PATH}

###########################################


ENV SERVICE_MAIN_CLASS ${application.mainClass}

COPY all-jars ${SERVICE_LIB_PATH}

#
# CMD exec form, needed so that the trap in run.sh works properly
# unfortunately you can't use variables in the CMD exec form
#
# https://docs.docker.com/engine/reference/builder/#cmd
#
ENTRYPOINT ["/opt/estimator/run.sh"]
# Informix With Docker Compose

Docker compose is a tool/service on top of docker that allows you to use a configuration (yaml) file to describe a set of containers you want to start manage together. It's a useful tool for repeatably starting containers with the same network/environment/settings.

## This example project
This project shows you how to use a simple yaml file to configure an Informix instance while showing how to use a few of the options available to tune and/or configure the service that comes up.

## Directions

### Make sure you have docker compose installed
On windows this comes pre-installed with Docker Desktop.  On Linux systems you might need to do an `apt-get` or `dnf install` to get docker-compose.  

### Clone this project onto your computer

The *.yml files are the primary files, but this example will use a config folder to highlight using scripts and mod files to customize the container on initialization.

### Run the initial setup

You can bring up your Informix database container using docker-compose with the following commands:

```bash
$> cd {download-location}/informix-db-examples/docker/simple-docker-setup
# Use -d to bring up your containers detached
$> docker-compose up -d
Creating network "simple-docker-setup_default" with the default driver
Creating ifx1 ... done

# Check the service is up and running
$> docker-compose.exe ps

Name              Command                  State                                                        Ports
---------------------------------------------------------------------------------------------------------------------------------------------------------------
ifx1   /opt/ibm/scripts/dinit /op ...   Up (healthy)   27017/tcp, 0.0.0.0:27018->27018/tcp, 27883/tcp, 8080/tcp, 0.0.0.0:9088->9088/tcp, 0.0.0.0:9089->9089/tcp

```
### Look at the docker-compose.yml file

`docker-compose` uses the `docker-compose.yml` file by default to define and start docker containers.  Looking at this file we can break down what it does

```yaml
version: '3'
services:
  informix-server1:
      image: "ibmcom/informix-developer-database"
      container_name: "ifx1"
      hostname: "ifx1"
      environment:
          LICENSE: 'accept'
          SIZE:    'small'
          RUN_FILE_POST_INIT: server-setup.sh
      ports:
         - 9088:9088
         - 9089:9089
         - 27018:27018
      volumes:
          - ${PWD}/config:/opt/ibm/config
```

Informix uses a mounted volume into `/opt/${vendor}/config` to specify a mount point for configuration files.  Inside of this directory you can place any SQL/shell/configuration files you need to customize your informix server.  Some special files are denoted as follows:

| File Name | Description |
|---|---|
|onconfig.mod| A list of key=value pairs of config parametes you want altered when the service is first initializaed|
|onconfig | A entire config file created that will be used instead of the default one|


In this example we use the onconfig.mod file in our config directory to enable basic user authentication.  By mapping our config directory, this file will be picked up when the service is first initialized.

Additionally in this example we have specified a `RUN_FILE_POST_INIT` which points at a shell script that is stored in the mapped config directory.  This shell script can run any command inside of the container.  This is really powerful when coupled with a .sql file such that you can initialize a database schema at initialization time.
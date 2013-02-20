#!/bin/sh

###
# #%L
# XmppBot Debian Packaging
# %%
# Copyright (C) 2012 - 2013 Bernd Kiefer <b.kiefer@raion.de>
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###

##################################################
# initialize
##################################################

BASENAME=enbot

init ${BASENAME}

##################################################
# Get the action & configs
##################################################

ACTION=

usage() {
cat <<EOF

usage: $0 options

Script to manage enbot:
  * start: starting enbot
 
OPTIONS:
  -h    Show this message
  -a    Action (start)

EOF
}

##################################################
# Retrieve the arguments
##################################################
while getopts "ha:p:" OPTION
do
  case $OPTION in
    h)
      usage
      exit 1
      ;;
    a)
      ACTION=$OPTARG
      ;;
    ?)
      echo "Illegal argument $OPTION=$OPTARG" >&2
      usage
      exit
      ;;
  esac
done

##################################################
# Do the action
##################################################
case "$ACTION" in
  start)
   check_java
   check_user $USER
   
    echo "starting enbot"
   
    # java -Duser.dir=$ENBOT_WORKING_DIR $CP de.raion.xmppbot.XmppBot $ENBOT_CONFIG_FILE
    java -Duser.dir=/etc/enbot/ -Dlogger.basedir=/var/log/enbot -classpath /usr/local/lib/enbot/libs/*: de.raion.xmppbot.XmppBot /etc/enbot/enbot.json
    exit 1
    ;;

  *)
    echo "unknown action $ACTION"
    usage
    exit
    ;;
esac

exit 0

check_user() {
        current_user=$(id -un)
        if [ "$current_user" != "$1" ]; then
                echo "needs to be run as user $1"
                exit 1
        fi
}

init() {
  # first argument = module name
  echo "init enbot.....hopefully"
  conf_file=/etc/enbot/$1.conf
  if [ -f "${conf_file}" ]; then
    . ${conf_file}
    echo ${conf_file}
	echo "workingdir=$ENBOT_WORKING_DIR"
  fi
}

check_java() {
  ##################################################
  # Setup JAVA if unset
  ##################################################
  if [ -z "$JAVA" ]
  then
    JAVA=$(which java)
  fi

  if [ -z "$JAVA" ]
  then
    echo "Cannot find a Java JDK. Please set either set JAVA or put java (>=1.6) in your PATH." 2>&2
    exit 1
  fi
}

# create the specified directory if necessary - arguments $MEMDUMP_DIR $USER $GROUP
check_directory() {
  # verify the existence of the directory
  if [ ! -d "$1" ]; then
    echo "creating directory '$1'"
    sudo mkdir -p $1
    sudo chown $2:$3 $1
  fi
}

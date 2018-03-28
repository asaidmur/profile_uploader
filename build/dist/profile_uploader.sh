#!/bin/bash


CONFIG_FILE=$1


# **************************************************
# ** log config                                   **
# **************************************************

YYYYMMDD_HHMMSS=`date '+%Y%m%d_%H%M%S'`

#export LOG_FILE=log/profile_uploader_$YYYYMMDD_HHMMSS.log


export LOGDIR=/calabrio/log/profile_uploader/


# **************************************************
# ** get jar files                                **
# **************************************************

for f in `find lib -type f -name "*.jar"` `find lib -type f -name "*.zip"`
do
  CLASSPATH=$CLASSPATH:$f
done


# **************************************************
# ** get props files                              **
# **************************************************

CLASSPATH=$CLASSPATH:config



#/java/bin/java -cp $CLASSPATH profile.uploader.ProfileUploader -config $CONFIG_FILE > $LOG_FILE
java -cp $CLASSPATH profile.uploader.ProfileUploader -config $CONFIG_FILE

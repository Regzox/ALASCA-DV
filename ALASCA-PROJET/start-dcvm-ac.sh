#!/bin/bash
if [ -z "$1" ]; then 
  echo usage: $0 jvmName
  exit
 fi
java -ea -cp 'jars/*' -Djava.security.manager -Djava.security.policy=dcvm.policy fr.upmc.datacenter.software.admissioncontroller.tests.distributed.AdmissionControllerCVM $1 config.xml
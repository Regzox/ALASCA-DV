IF [%1]==[] ECHO usage: %1 jvmName
IF [%1]==[] EXIT
REM java -ea -cp 'jars/*' -Djava.security.manager -Djava.security.policy=dcvm.policy fr.upmc.datacenter.software.admissioncontroller.tests.distributed.AdmissionControllerCVM %1 config.xml
java -ea -cp jars/* -Djava.security.manager -Djava.security.policy=dcvm.policy fr.upmc.datacenter.software.admissioncontroller.tests.distributed.AdmissionControllerCVM %1 config.xml
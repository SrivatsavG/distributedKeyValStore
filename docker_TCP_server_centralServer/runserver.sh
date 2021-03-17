#!/bin/bash
# Dummy jar file
java -jar GenericNode.jar


#TCP Server - Centralized membership using TCP
#java -jar GenericNode.jar ts <server port number> <membership_server_IP> <membership_server_port>

java -jar GenericNode.jar ts 6002 172.17.0.5 4410




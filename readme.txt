Programming language : Java
IDE: Netbeans

Membership tracking methods: FD,T (Dynamic membership with static file & Centralized membership server with TCP)

Other features implemented
1. Multithreaded membership server and TCP server
2. Threadpools
3. Concurrent hashmaps

A.Build project using Netbeans

	---> To test the script without docker, follow instructions under B.
	---> To test the script with docker, follow instructions under C.

B.Instructions to run the project without docker

	----------------STARTING SERVERS-----------------
	B.0. cd to the folder ( target folder ) with the GenericNode.jar file.
	
		---> To start the dynamic file membership tracking, follow B.1.
		---> To start the Centralized TCP Membership server, follow B.2. 
	
	B.1. Static file membership tracking DYNAMIC
		B.1.a. Running TCP servers
			java -jar GenericNode.jar ts <server port number>
		B.1.b. NOTE 
			The membership file is located in tmp/membership.txt. An exit command from the client to a server shuts
			down the server and not the entire network. Only that node is deleted from the membership list and the distributed system 				remains online with all the other nodes. Therefore to ensure that membership list is maintained properly across different 				tests, the tester has to shut down all the individual nodes one by one so that the list is finally empty. The other option 
			is to delete the contents of the file manually between tests. 
	
	B.2. TCP Membership server
		B.1.a. Running membership server
			java -jar GenericNode.jar ms 4410
		B.1.b.	Running TCP servers
			java -jar GenericNode.jar ts <server port number> localhost 4410
			
			NOTE: Note that the server port number cannot be 4410
						
	---------------RUNNING CLIENTS-------------------
	B.3. Running client
		PUT   : java -jar GenericNode.jar tc <server IP> <sever port number> put <key> <value>
		GET   : java -jar GenericNode.jar tc <server IP> <sever port number> get <key>
		DELETE: java -jar GenericNode.jar tc <server IP> <sever port number> del <key>
		STORE : java -jar GenericNode.jar tc <server IP> <sever port number> store 
		EXIT  : java -jar GenericNode.jar tc <server IP> <sever port number> exit 
		

C. Instructions to run the project on docker
	
	C.0. Copy and paste the jar file in the 4 folders: docker_client, docker_TCP_server_file, docker_TCP_server_centralServer & docker_membership_server. 
		
		---> To start the dynamic file membership tracking, follow C.1.
		---> To start the Centralized TCP Membership server, follow C.2. 

	----------------STARTING SERVERS-----------------

	C.1. Static file membership tracking DYNAMIC
	
		C.1.a. Open the runserver.sh under the docker_TCP_server_file folder. 
		C.1.b. Uncomment the command and enter a port. Build the container.		        	
		C.1.c. cd to the root folder which contains the "tmp" folder.
		C.1.d. Run the docker container using the following command to mount the tmp directory in the container. This directory would be used to store the membership.txt file.
		       	
		       	sudo docker run -it -v "$(pwd)":/tmp -d --rm <server name>
		       			
		       Note the ip address and the port of the server. We will refer to the former as server_IP and the latter as server_port
	
	
	C.2. TCP Membership server
		
		C.2.a. The runserver.sh in the docker_membership_server folder is configured to start the membership server in port 4410.
		
		C.2.b. Build the membershipserver container, run it and find its IP address using instructions provided in pages 5 & 6 of assignment2.pdf.
		
		C.2.c. Copy the IP address. We will refer to this IP address as membership_server_IP
		
		C.2.d. Open the runserver.sh under the docker_TCP_server_centralServer folder
		
		C.2.e  Uncomment the command and enter a port for the server, the membership_server_IP and the membership server's port (4410). Build the container and run the container. Note its IP address and port.
		
	
		---------------RUNNING CLIENTS-------------------
	C.3. Running client container
	
		C.3.a. Build the client docker container from the docker_client folder.
		
		C.3.b. The tester can perform any of these operations on the TCP servers
		PUT   : java -jar GenericNode.jar tc <server IP> <sever port number> put <key> <value>
		GET   : java -jar GenericNode.jar tc <server IP> <sever port number> get <key>
		DELETE: java -jar GenericNode.jar tc <server IP> <sever port number> del <key>
		STORE : java -jar GenericNode.jar tc <server IP> <sever port number> store 
		EXIT  : java -jar GenericNode.jar tc <server IP> <sever port number> exit 
		
		C.3.c. To test the bigtest_tc.sh script
			Open the bash terminal of the container. Run the script by passing two arguments 1) Server IP 2)Server port
		
		
	
		
		
		
		
		
			

		

       

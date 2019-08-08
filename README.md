Revolute-Fund-Transfer RESTFul API
-----------------------------------------------------------------

Tools and Technologies Used:
•	java 8
•	junit 4.12
•	log4j2 2.11.1
•	jetty 2.21
•	http client 1.5.6
•	jersey 2.27
•	maven
•	h2 1.4.199
•	postman

Steps to run this project:
1.	go to root directory
2.	run this maven command : mvn clean package
3.	go to target directory. this will create a fat jar i.e. revolut-fund-transfer.jar
4.	run this java command from target directory: java -jar revolut-fund-transfer.jar

Logs will be generated at this location : C:\revolut-fundtransfer-logs

REST API End points.
Base URL: http://localhost:9090/revolut/fundtransfer

GET	/usr/all	- To get all users
GET	/usr/{name}	- To get a particular user by name
POST	/usr/{create}	- To create a user
PUT	/usr/{userId}	- To update a user by ID
DELETE	/usr/{userId}	- To delete a user by ID
GET	/ac/all	- To get all accounts
GET	/ac/{accountId}	- To get an account by ID
GET	/ac/{accountId}/balance	- To get an account balance by ID
POST	/ac/create	- To create an account
PUT	/ac/{accountId}/deposit/{amount}	- To deposit an amount to an account by ID
PUT	/ac/{accountId}/withdraw/{amount}	- To withdraw an amount from an account by ID
DELETE	/ac/{accountId}	- To delete an account by account ID
POST	/trans/	- To transfer fund from one account to another

Sample Calls from postman:

To get a user
GET: http://localhost:9090/revolut/fundtransfer/usr/JOHN
Content-Type : application/json
Response : {
    "userId": 1,
    "name": "JOHN",
    "emailId": "john123@gmail.com"
}
------------------------------------------------------------------
To get all users
GET: http://localhost:9090/revolut/fundtransfer/usr/all
Content-Type : application/json
Response : [
    {
        "userId": 1,
        "name": "JOHN",
        "emailId": "john123@gmail.com"
    },
    {
        "userId": 2,
        "name": "MERRY",
        "emailId": "merry654@gmail.com"
    },
    {
        "userId": 3,
        "name": "LUCY",
        "emailId": "lucy369@gmail.com"
    },
    {
        "userId": 4,
        "name": "ROSE",
        "emailId": "rose123@gmail.com"
    },
    {
        "userId": 5,
        "name": "TAYLOR",
        "emailId": "taylor654@gmail.com"
    },
    {
        "userId": 6,
        "name": "PONTING",
        "emailId": "ponting001@gmail.com"
    },
    {
        "userId": 7,
        "name": "PELLE",
        "emailId": "pelle369@gmail.com"
    }
]
------------------------------------------------------------------
To create a user
POST: http://localhost:9090/revolut/fundtransfer/usr/create
Content-Type : application/json
Request : {  
  "name":"test1",
  "emailId":"test1@gmail.com"
} 
Response : {
    "userId": 8,
    "name": "test1",
    "emailId": "test1@gmail.com"
}
------------------------------------------------------------------
To get all accounnts
GET : http://localhost:9090/revolut/fundtransfer/ac/all
Content-Type : application/json
Response : [
    {
        "accountId": 1,
        "userName": "JOHN",
        "balanceAmount": 1000.0000,
        "currencyCode": "USD"
    },
    {
        "accountId": 2,
        "userName": "MERRY",
        "balanceAmount": 2000.0000,
        "currencyCode": "USD"
    },
    {
        "accountId": 3,
        "userName": "LUCY",
        "balanceAmount": 3000.0000,
        "currencyCode": "EUR"
    },
    {
        "accountId": 4,
        "userName": "ROSE",
        "balanceAmount": 4000.0000,
        "currencyCode": "EUR"
    },
    {
        "accountId": 5,
        "userName": "HAPPY",
        "balanceAmount": 5000.0000,
        "currencyCode": "GBP"
    },
    {
        "accountId": 6,
        "userName": "TAYLOR",
        "balanceAmount": 6000.0000,
        "currencyCode": "GBP"
    },
    {
        "accountId": 7,
        "userName": "PONTING",
        "balanceAmount": 7000.0000,
        "currencyCode": "USD"
    },
    {
        "accountId": 8,
        "userName": "PELLE",
        "balanceAmount": 8000.0000,
        "currencyCode": "USD"
    }
]
------------------------------------------------------------------
To create an account
POST : http://localhost:9090/revolut/fundtransfer/ac/create
Content-Type : application/json
Request : {  
  "userName":"test1",
  "balanceAmount": 2000,
  "currencyCode":"EUR"
} 

Response : {
    "accountId": 9,
    "userName": "test1",
    "balanceAmount": 2000.0000,
    "currencyCode": "EUR"
}
------------------------------------------------------------------
To transfer insufficient amount
POST: http://localhost:9090/revolut/fundtransfer/trans
Content-Type : application/json
Request : {
	"currencyCode":"EUR",
	"amount":"12345.68940",
	"fromAccountId":"3",
	"toAccountId":"4"
}
Response :
{
    "errorCode": "Not enough Fund from source Account "
}
------------------------------------------------------------------
To transfer sufficient amount
POST: http://localhost:9090/revolut/fundtransfer/trans
Content-Type : application/json
Request: {
	"currencyCode":"EUR",
	"amount":"125.68940",
	"fromAccountId":"3",
	"toAccountId":"4"
}
Response : {
    "message": "FUND TRANSFERRED SUCCESSFULLY"
}

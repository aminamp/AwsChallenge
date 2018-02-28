# AwsChallenge

### Exercise Description

Using Java, write a micro service that invokes AWS elastic search and makes it available using API gateway and Lambda.



### Steps Involved:
1. As given dataset (http://askebsa.dol.gov/FOIA%20Files/2016/Latest/F_5500_2016_Latest.zip) is in CSV format, we should corvert it to acceptable input format for ElasticSearch which is JSON format:
    * Method 1: convert CSV to JSON manually using convertCSVtoJSON.java and upload converted file using curl -XPOST command
    * Method 2: convert and upload CSV file to ElasticSearch using Logstash and its plugin logstash-output-amazon_es available at https://github.com/awslabs/logstash-output-amazon_es

2. Create a Domain in Amazon Elasticsearch Service and upload JSON formated dataset
3. Create a Lambda function (LambdaFunctionHandler.java) to handle queries and export it as a JAR file and upload it to AWS Lambda Sevices

4. Create a new API in AWS API Gateway to trigger Lambda created in step 3 using HTTP request. using the following formats, user can trigger Lambda function to search inside dataset based on:
    * Search by Plan Name:https://id7ug0wgc6.execute-api.us-east-2.amazonaws.com/test/plans?planName=X
    * Search by Sponsor Name:https://id7ug0wgc6.execute-api.us-east-2.amazonaws.com/test/plans?sponsorName=X
    * Search by Sponsor State: https://id7ug0wgc6.execute-api.us-east-2.amazonaws.com/test/plans?sponsorState=X



for example: https://id7ug0wgc6.execute-api.us-east-2.amazonaws.com/test/plans?sponsorState=NY



### TODO:
1. Tune AWS ElasticSearch parameters and settings to imporve the response time
2. Modify Lambda function to handle complex queries like planName=X & sponsorName=Y

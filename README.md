# irfanstore-ratelimit-route-service

This is the sample rate limiting routing service written to run in the Pivotal Cloud Foundry. This application uses Redis to store the rate limit information. It uses the client IP + URL as a key to store attempts.


## Running application inside Pivotal Cloud Foundry
### Service Binding
Service binding is defined in the manifest.yml file. Create the following services inside the Cloud Foundry with the name provided as below:

* `rediscloud` service name: `ostore-redis`

### Environment Variables
Set the following environment variables in the manifest.yml file:
* `RATE_LIMIT_EXPIRY` defines the expiry of the time of attempts in minutes. 
* `RATE_LIMIT_ATTEMPT_ALLOWED` defines the maximum attempts allowed in minutes.

### Push the application
After creating the services push the application using the `cf push` command. 


### Bind the client application
To bind the client application to route the call through the routing service do the following steps:
* Create service instance. Run the following command `cf cups ostore-ratelimit-routing-service -r https://irfanstore-ratelimit-route-service.cfapps.io` 
* Bind the service. Run the following command: `cf bind-route-service cfapps.io ostore-ratelimit-routing-service --hostname myClientApplication`


# irfanstore-ratelimit-route-service

This is the sample rate limit routing service written to run in the Pivotal Cloud Foundry. This application uses Redis to store the rate limit information. It uses the client IP + URL as a key to store attempts.


## Running application inside Pivotal Cloud Foundry
### Service Binding
Service binding is defined in the manifest.yml file. Create the following services inside the Cloud Foundry with the name provided as below:

* `rediscloud` service name: `ostore-redis`
* To create the service run the following command `cf cups ostore-ratelimit-routing-service -r https://irfanstore-ratelimit-route-service.cfapps.io`

### Environment Variables
Set the following environment variables in the manifest.yml file:
* `RATE_LIMIT_EXPIRY` defines the expiry of the time of attempts in minutes. 
* `RATE_LIMIT_ATTEMPT_ALLOWED` defines the maximum attempts allowed in minutes.

### Push the application
After creating the services push the application using the `cf push` command. 


### Bind the client application
To bind the application route the call through the routing service run the following command: `cf bind-route-service cfapps.io ostore-ratelimit-routing-service --hostname myClientApplication`


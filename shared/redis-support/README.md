Redis Support
-----

Redis support is a library support for redis cache.

## Adding Redis support to your service

You have to add Redis support library to `settings.gradle` and `build.gradle` as shown below.

Add following to `settings.gradle`

```
includeBuild '../../shared/redis-support'
```

Add following to `build.gradle` dependencies section.

```
implementation "com.otg.tech:redis-support"
```

To enable Redis support in your project, add below property in application.properties file.

```
## Redis cache support
au.redis.cache-enable=true
```

Create redis.properties file in your resources folder
Add below properties in redis.properties to establish the connection.

```
au.redis.connector-port=6379
au.redis.connector-host=localhost
au.redis.connector-username=<username>
au.redis.connector-password=<password>
```

## Redis Support Usage

Finally, inject RedisService bean wherever you need.
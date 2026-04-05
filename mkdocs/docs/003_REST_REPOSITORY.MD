# 03. Rest Repository

### Repository
requirement `interface`
- [RestRepository](../../spring-common/src/main/java/com/asliri/core/applications/rest/repository/RestRepository.java)

support method
- [@Get](../../spring-common/src/main/java/com/asliri/core/applications/rest/annotations/Get.java)
- [@Post](../../spring-common/src/main/java/com/asliri/core/applications/rest/annotations/Post.java)
- [@Put](../../spring-common/src/main/java/com/asliri/core/applications/rest/annotations/Put.java)
- [@Patch](../../spring-common/src/main/java/com/asliri/core/applications/rest/annotations/Patch.java)
- [@Delete](../../spring-common/src/main/java/com/asliri/core/applications/rest/annotations/Delete.java)

annotation method parameter


| Parameter         | Default Value             | Description 
|-------------------|---------------------------|-------------
| `key`               | ""                        | 
| `url`               | ""                        | 
| `connect`           | -1                        | timeout create connection
| `request`           | -1                        | timeout upload/transfer body
| `read`              | -1                        | timeout read response
| `contentType`       | application/json          | 
| `throwException`    | true                      | 


### arg key

| Placeholder (key & url)          | Description                                                         
|----------------------------------|---------------------------------------------------------------------
| `PLAIN TEXT`                     | read from plain text                                                
| `${app.value}`                   | read from evironment variable/application.properties/application.yml 
| `D{app.value}` or `d{app.value}` | read from datasource `app.value` as key [RestConfigBaseKey](../../spring-common/src/main/java/com/asliri/core/applications/rest/model/RestConfigBaseKey.java)                 

implement [RestConfigBaseKey](../../spring-common/src/main/java/com/asliri/core/applications/rest/model/RestConfigBaseKey.java)
```java
@Configuration
public class RestConfig {

    @Autowired
    ConfigRepository configRepository; // any repository table configuration

    @Bean
    public RestConfigBaseKey restConfigBaseKey() {
        return new RestConfigBaseKey() {
            @Override
            public String getValue(String key) {
                return configRepository.findById(key).orElseThrow().getValue();
            }
        };
    }
}
```

### arg url

support uri template
- @PathVariable

example

```java

@Get(key = "https://pokeapi.co/api/v2/pokemon", url = "/{name}")
Object result = callHardcodeWithPathVariable(@PathVariable("name") String name);

@Get(url = "https://pokeapi.co/api/v2/pokemon/{name}")
Object result = callHardcodeWithPathVariable(@PathVariable("name") String name);
```
## Method Argument
- [HttpHeaders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html)
- [PathVariable](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/PathVariable.html)
- Object as request body

## Handling Response

exception based on parameter `throwException`

- [HttpStatusCodeException](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/HttpStatusCodeException.html)
- [ResourceAccessException](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/ResourceAccessException.html)
- [BadGatewayException](../../spring-common/src/main/java/com/asliri/core/applications/exceptions/BadGatewayException.java)

```java
// snippet handling error rest executor

} catch (HttpStatusCodeException e) {
    String bodyErrorString = e.getResponseBodyAsString();
    if (isRestResultParameterize) {
        return new ResponseWrapper<>(e);
    }
    log.error("error code {}, {}, body: {}", e.getStatusCode(), e.getStatusText(), bodyErrorString);
    throw e;
} catch (ResourceAccessException e) {
    if (isThrowResourceAccessException) {
        throw new BadGatewayException();
    }
    if (isRestResultParameterize) return new ResponseWrapper<>(e);
    log.error("resource access exception {}", e.getMessage());
    throw e;
}

```


response object/model
- generic object
- [ResponseWrapper](../../spring-common/src/main/java/com/asliri/core/applications/rest/model/ResponseWrapper.java)
  
| response         | result                              | Description
|------------------|-------------------------------------|-------------
| generic object   | convert response to result object   |  `throwException=true`, use global handling `@RestControllerAdvice`   
| ResponseWrapper  | convert response to ResponseWrapper | `ResponseWrapper` support supplier handling

snipet supplier `ResponseWrapper.java`

```java
public <X extends Throwable> T bodyOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    return Optional.ofNullable(body).orElseThrow(exceptionSupplier);
}

public <X extends Throwable> T bodyOrElseThrow(Supplier<? extends X> exceptionSupplier, int... codes) throws X {
    boolean codeMatch = isCodeMatch(codes);
    if (isError() && codeMatch) throw exceptionSupplier.get();
    return Optional.ofNullable(body).orElseThrow(exceptionSupplier);
}

public <X extends Throwable> void isErrorAndThrow(int... codes) throws X {
    boolean codeMatch = isCodeMatch(codes);
    if (isError() && codeMatch) throw exception;
}

public <X extends Throwable> void isErrorAndThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (isError()) throw exceptionSupplier.get();
}

public <X extends Throwable> void isErrorAndThrow(Supplier<? extends X> exceptionSupplier, int... codes) throws X {
    boolean codeMatch = isCodeMatch(codes);
    if (isError() && codeMatch) throw exceptionSupplier.get();
}
```

## Example

### Json
default content type `application/json`

```java
public interface PokemonRestRepository extends RestRepository {
    
    @Get(url = "https://pokeapi.co/api/v2/pokemon/ditto")
    String callHardcode();

    @Get(url = "https://pokeapi.co/api/v2/pokemon/{name}")
    String callHardcodeWithParam(@PathVariable("name") String name);

    @Get(key = "https://pokeapi.co/api/v2", url = "/pokemon/ditto")
    String callHardcodeCombine();

    @Get(key = "${pokemon.base}", url = "${pokemon.ditto}")
    String callEnvironment();

    @Get(key = "${pokemon.base}", url = "/pokemon/ditto")
    ResponseWrapper<String> callEnvironmentCombine();

    @Get(key = "D{POKEMON}", url = "D{POKEMON_DITTO}")
    ResponseWrapper<String> callDatabase();

    @Get(key = "D{POKEMON}", url = "/pokemon/ditto")
    ResponseWrapper<String> callDatabaseCombine();

    @Get(key = "POKEMON", url = "/pokemon/ditto")
    ResponseWrapper<String> callDatabaseAsKey();
}

```
### Multipart
content type `multipart/form-data`
```java
    @Post(key = "${liveness.active.url}", url = "/", contentType = MediaType.MULTIPART_FORM_DATA_VALUE, read = 60000)
    ResponseWrapper<VerifyLivenessActiveResponse> livenessActive(HttpHeaders headers, MultiValueMap<String, Object> request);
```
```java
    // build body
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.set("trx_id", request.getTrxId());
    body.add("gestureset", request.getGesturesSet());
    for (MultipartFile file : request.getFile()) {
        // create binary payload
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getName();
            }
        });
    }
    ResponseWrapper<VerifyLivenessActiveResponse> responseWrapper = agrOnboardingRestRepository.livenessActive( body);
```
### Form Urlencoded
content type `x-www-form-urlencoded`
```java
    @Post(url = "https://httpbin.org/post",  contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String urlEncoder(MultiValueMap form);
```
```java
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.set("name", "john doe");
    body.add("date", "today");
    String response = httpBinRestRepository.urlEncoder(body);
```
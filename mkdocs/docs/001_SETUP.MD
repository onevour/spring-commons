## 01. Setup

add dependency `build.gradle`
```groovy
maven { 
    url "https://dev-asli-nexus.mypoc.id/repository/maven-asliri/" 
}
```

### spring-commons

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'com.asliri.core:spring-common:2.0.0.7'
}
```

### spring-commons-jpa

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation "org.springframework.boot:spring-boot-starter-data-jpa"
    implementation 'com.asliri.core:spring-common:2.0.0.7'
    implementation 'com.asliri.core:spring-common-jpa:2.0.0.2'
}
```

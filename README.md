### How to use
Project level build.gradle
```groovy
repositories {
  maven {
    url "https://gurunun.shop:6302/repository/maven-releases/"
  }
}
```
Module level build.gradle
```groovy
dependencies {
  implementation 'noh.jinil.android.log:LogFormat:1.0.4@aar'
}
```
In your code (using retrofit interceptor)
```kotlin
import noh.jinil.android.log.LogFormat

val loggingInterceptor = HttpLoggingInterceptor { message ->
    LogFormat.httpResponse(message)
}.apply {
    setLevel(HttpLoggingInterceptor.Level.BODY)
}

val httpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

Retrofit.Builder()
    .baseUrl(baseURL)
    .client(httpClient)
    .build()
    .create(YourApi::class.java)
```

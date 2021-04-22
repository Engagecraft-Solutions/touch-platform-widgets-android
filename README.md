# Touch Platform SDK

## Demo app

To run the demo app, clone the repo, and build an app (use `debug` buildType).

## Requirements

## Installation

Touch Platform SDK is available through public GitHub Maven repository.

To acceess the repo you need to create an access token [here](https://github.com/settings/tokens/new). You only need the `read:packages` permission.

To install it you need to add it to your dependency list and sync gradle:

```groovy
// app/build.gradle

// add Touch Platform SDK Maven repository
android {
  repositories {
    maven {
      url = uri("https://maven.pkg.github.com/engagecraft-solutions/touch-platform-widgets-android")
      credentials {
        username "__YOUR_USERNAME__"
        password "__YOUR_TOKEN__" // from here: https://github.com/settings/tokens/new
      }
    }
  }
}

// add a dependency
dependencies {
  ...
  implementation 'com.engagecraft:touchplatform-sdk:0.9.0'
}
```

## Usage

### SDK Initialisation

You **must** initialize Touch Platform SDK. It doesn't matter when it is initialized but it mus be **before** widget creation.

```kotlin
// e.g. on Application.onCreate() or Activity.onCreate()
{
  TouchPlatformSDK.init(
    __YOU_CLIENT_ID__, // a provided Client ID : String
    "en", // (optional) language : String
    false, // (optional) preview mode : Boolean
    // (optional) listener for opening login flow (if required by the widget)
    object : TouchPlatformSDK.Listener {
      override fun showLogin() {
        openLoginScreen()
      }
    }
  )
}

```

Also you may need to re-initialize the SDK if you want to change something dynamically e.g. language. The widgets must be recreated too.

#### Preview mode

SDK supports Preview Mode. Once enabled you can load a widget that is not published yet. Votes will not be submitted so you can event try the published widget too. Keep in mind that sharing will not work because there will be no vote on the backend.

### Widget Initalisation 

Create a widget with provided Widget ID:

```kotlin
val widget = TouchPlatformSDK.getWidget(
  this, // a context used to create a widget
  __WIDGET_ID__, // Widget id
  getAppLink() // (optional) A link that will be used for sharing (once a user clicks on the shared content).
)
```

> It is a good idea to keep a reference to the widget untill you don't need it anymore.

#### App Link for sharing

You may provide an link for shared widget vote. It may be a regular web link or a link that app handles as an app link, e.g. "https://my-domain.com/widget". Or you may use a custom scheme, e.g. "com.my.app://widget".
You will get a widget ID as the URL hash property, e.g. "https://my-domain.com/widget#x-xxxxxxxxxx"

### Add Widget to the layout

You should add the widget to the layout: 
```kotlin
  parent.addView(widget)
```

## User management
An app should inform Touch Platform SDK anytime a user logs in into the app or logs out (if an app uses user authentication). 

> Don't forget to implement `TouchPlatformSDK.Listener` on `TouchPlatformSDK.init()` to let the user to login from the widget (if applicable to the app).

### Login
After user logs in, call:
```kotlin
TouchPlatformSDK.login(userID) // userId: String
```

> Also call this method just after the SDK initialization if the user is already logged in.

### Logout
After user logs out, call: 
```kotlin
TouchPlatformSDK.logout()
```

### Login requests

If a Widget SDK needs a user id and the app did not provide one yet, then SDK will call `TouchPlatformSDK.Listener.showLogin()` method on `TouchPlatformSDK.Listener` you provided on SDK initialisation. 
In that case the app should handle the request: 
1. Start user login flow if needed.
2. After user logs in provide user id to Touch Platform SDK by calling `TouchPlatformSDK.login(userID)`.


## Java support

The SDK is written on Kotlin language and all the examples uses Kotlin language. To use the SDK on Java you need to use the library this way (throught the Companion object):
```java
// uses Java 1.8 features

// initialization
TouchPlatformSDK.Companion.init(__CLIENT_ID__, "en", false, () -> {
  openLoginScreen()
});

// widget creation
TouchPlatformSDK.Companion.getWidget(this, __WIDGET_ID__, "https://...");

// user login
TouchPlatformSDK.Companion.login("");

// user logout
TouchPlatformSDK.Companion.logout();
```

## License

Copyright Engagecraft Solutions

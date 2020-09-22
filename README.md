<!-- ⚠️ This README has been generated from the file(s) "blueprint.md" link - https://github.com/andreasbm/readme ⚠️--><p align="center">
  <img src="https://github.com/Gkemon/App-Watermark/blob/master/water%20mark.png" alt="Logo" width="150" height="150"  />
</p>
<h1 align="center">App watermark for Android</h1>
 <p align="center">
		<a href="https://github.com/Gkemon/App-Watermark"><img alt="Maintained" src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" height="20"/></a>
	<a href="https://github.com/Gkemon/App-Watermark"><img alt="Maintained" src="https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg" height="20"/></a>
	<a href="https://jitpack.io/#Gkemon/App-Watermark"><img alt="Maintained" src="https://jitpack.io/v/Gkemon/App-Watermark.svg" height="20"/></a>

<p align="center">
  <b>Create an watermark for your Android app.</b></br>
  <p align="center">Suppose you have a freemium app and wanna show an watermark in your app after a free trial to lessen it's user experience to buy the premium version. Then this library is compatible for your use-case. Not only this use-case, you can use it in a lot of use-cases where you wish to lessen the content access capacity of your users or preview a content all over the app content (<a href="https://stackoverflow.com/questions/59132492/how-to-create-an-overlay-for-app-as-an-watermark-in-android-pragmatically"><i>A revenge of downvoting my question</i></a> :stuck_out_tongue: )<p>
</p>

<br />


<p align="center">
  <img src="https://github.com/Gkemon/App-Watermark/blob/master/watermark.gif" alt="Demo" width="800" /> 
</p>

* **Simple**: Extremely simple to use. For using <b>Step Builder Design Patten</b> undernath,here IDE greatly helps developers to complete the steps for creating an watermark.
* **Powerful**: Customize almost everything. It uses a layout resource which will be shown in your app as an watermark. So as an developer,you are very used to with layout resources and you are very powerful to customize it.
* **Transparent**: It shows logs,success-responses, failure-responses , that's why developer will nofity any event inside the process. 

<details>
<summary>📖 Table of Contents</summary>
<br />

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#table-of-contents)

## ➤ Table of Contents

* [➤ Installation](#-installation)
* [➤ Getting Started](#-getting-started)
* [➤ License](#-license)
</details>


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#installation)

## ➤ Installation

**Step 1**. Add the JitPack repository to your root ```build.gradle``` at the end of repositories
```
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2**. Add the dependency [![](https://jitpack.io/v/Gkemon/App-Watermark.svg)](https://jitpack.io/#Gkemon/App-Watermark)
```
dependencies {
        implementation 'com.github.Gkemon:App-Watermark:1.0.0'
}
```	
[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#getting-started-quick)



## ➤ Getting Started

You can generate app <b>Watermark</b> using just 2 mandatory components.
* <b><i>AppCompatActivity.</i></b> (From which activity you wanna acatually show your watermark, <i>e.g:</i> ```MainActivity```) **[Mandatory]**
* <b><i>Layout resource.</i></b> (The layout which you wanna show as the watermark, <i>e.g:</i> ```R.layout.layout_water_mark```) **[Mandatory]**
* <b><i>Background color.</i></b> ( <i>e.g:</i> ```R.color.deepRed```. If you skip it then it will consider the background color of your defined layout resourc.) **[Optional]**
* <b><i>Opacity.</i></b> (Default opacity of background color is 50 if you don't set opacity manually.Best practice is never set it above 80 because it will decrease the transparent level of your background color, which might be cause not showing the main UI or content properly) **[Optional]**
</i></b>


### By Java:

#### First initialize it from an activity from where you want to show the watermark- 

```java
AppWaterMarkBuilder.doConfigure()
                .setAppCompatActivity(MainActivity.this)
                .setWatermarkProperty(R.layout.layout_water_mark)
		/* You can also set opacity or opacity with a default background color.
		 * Just call like that ".setWatermarkProperty(R.layout.layout_water_mark, 40, R.color.colorAccent)" */
                .showWatermarkAfterConfig(new WatermarkListener() { /*This callback is also optional here */
                    @Override
                    public void onSuccess() {
                     Log.d(TAG, "Successfully showing water mark");
                    }

                    @Override
                    public void onFailure(String message, Throwable throwable) {
		     Log.d(TAG, "Failed: "+message");
                    }

                    @Override
                    public void showLog(String log, @Nullable Throwable throwable) {
 		     Log.d(TAG, "Log: "+log");
                    }
                });
```
#### Then you can hide and show it from any where in your app-
```
  /* For hiding the watermark without callback*/
  AppWaterMarkBuilder.hideWatermark() 
 
  /* For hiding the watermark with callback*/
  AppWaterMarkBuilder.hideWatermark(new WatermarkListener() {
                    @Override
                    public void onSuccess() {
                     Log.d(TAG, "Successfully showing water mark");
                    }

                    @Override
                    public void onFailure(String message, Throwable throwable) {
		     Log.d(TAG, "Failed: "+message");
                    }

                    @Override
                    public void showLog(String log, @Nullable Throwable throwable) {
 		     Log.d(TAG, "Log: "+log");
                    }
                })
    
    /* For showing the watermark without callback*/
  AppWaterMarkBuilder.showWatermark() 
 
  /* For showing the watermark with callback*/
  AppWaterMarkBuilder.showWatermark(new WatermarkListener() {
                    @Override
                    public void onSuccess() {
                     Log.d(TAG, "Successfully showing water mark");
                    }

                    @Override
                    public void onFailure(String message, Throwable throwable) {
		     Log.d(TAG, "Failed: "+message");
                    }

                    @Override
                    public void showLog(String log, @Nullable Throwable throwable) {
 		     Log.d(TAG, "Log: "+log");
                    }
                })
```   

### By Kotlin:

#### First initialize it from an activity from where you want to show the watermark- 

```kotlin
                 doConfigure()
                .setAppCompatActivity(this@MainActivity)
                .setWatermarkProperty(R.layout.layout_water_mark)
		/* You can also set opacity or opacity with a default background color.
		 * Just call like that ".setWatermarkProperty(R.layout.layout_water_mark, 40, R.color.colorAccent)" */
                .showWatermarkAfterConfig(object : WatermarkListener { /*This callback is also optional here */
                    override fun onSuccess() {
                        Log.d(TAG, "Successfully showing water mark")
                    }

                    override fun onFailure(message: String?, throwable: Throwable?) {
                        Log.d(TAG, "Failed: $message")
                    }

                    override fun showLog(log: String?, throwable: Throwable?) {
                        Log.d(TAG, "Log: $log")
                    }
                })
```
#### Then you can hide and show it from any where in your app-
```
   /* For hiding the watermark without callback*/
   AppWaterMarkBuilder.hideWatermark() 
 
   /* For hiding the watermark with callback*/
   AppWaterMarkBuilder.hideWatermark(new WatermarkListener() {object : WatermarkListener {
                override fun onSuccess() {
                    Log.d(MainActivity.TAG, "Successfully showing water mark")
                }

                override fun onFailure(message: String?, throwable: Throwable?) {
                    Log.d(MainActivity.TAG, "Failed: $message")
                }

                override fun showLog(log: String?, throwable: Throwable?) {
                    Log.d(MainActivity.TAG, "Log: $log")
                }
            })
    
   /* For showing the watermark without callback*/
    AppWaterMarkBuilder.showWatermark() 
 
   /* For showing the watermark with callback*/
    AppWaterMarkBuilder.showWatermark(object : WatermarkListener {
                override fun onSuccess() {
                    Log.d(MainActivity.TAG, "Successfully showing water mark")
                }

                override fun onFailure(message: String?, throwable: Throwable?) {
                    Log.d(MainActivity.TAG, "Failed: $message")
                }

                override fun showLog(log: String?, throwable: Throwable?) {
                    Log.d(MainActivity.TAG, "Log: $log")
                }
            })
```   
  
[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#templates)

<p>
  <a href="https://www.linkedin.com/in/gk-mohammad-emon-0301b7104" rel="nofollow noreferrer">
    <img src="https://i.stack.imgur.com/gVE0j.png" alt="linkedin"> LinkedIn
  </a> &nbsp; 
  <a href="emon.info2013@gmail.com">
   <img width="20" src="https://user-images.githubusercontent.com/5141132/50740364-7ea80880-1217-11e9-8faf-2348e31beedd.png" alt="inbox"> Inbox
  </a>
</p>

<div>Icon is made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>


## ➤ License

The source code is licensed under the [Apache License 2.0](https://github.com/Gkemon/App-Watermark/blob/master/LICENSE). 


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#license)



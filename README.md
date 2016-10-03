# JPost (Java and Android class communication library)
[![Build Status](https://travis-ci.org/janishar/JPost.svg?branch=master)](https://travis-ci.org/janishar/JPost)
[ ![Download](https://api.bintray.com/packages/janishar/mindorks/jpost/images/download.svg) ](https://bintray.com/janishar/mindorks/jpost/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-JPost-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/4432)
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=102)](https://opensource.org/licenses/Apache-2.0)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Intro
### This library is designed for the communication between classes in java / android by sending and receiving messages. Messages can be any object.
The design of this library is such that the modularity of the code is enhanced and provide a controlled system for sending and receiving messages. One of the key advantages of this library is that the message handling can be done both synchronusly(if the sender thread is same as the receiver thread) or asynchronously as provided. All the subscribing classes are holded with weakreferences, hense the memory leak do not take place. The usecases in which this library's power can be understood is when compared to other pub/sub libraries. Situations in which many instances of a single class require to process messages based on the sender. Example: A class wants to send message to few of the instances of the same class. The schema diagram provided below will provide a better insight.
<hr />

# Library Design Overview
### The channels through which communication can be done is categorised into three categories:
1. **Default Channel**: This is the prebuilt channel and allows global communication. When subscribed to this channel, the class can send messages to all the subscribed classes with message type, on this channel.
2. **Public Channel**: This channel is designed for filtered communication. Public channels are created and the subscribers of this channel can receive messages broadcasted to this channel. The messages can also be send to selected subscribes.
3. **Private Channel**: This channel is designed to control the access to the channel. The private channels need to be created and stores the owner information. Only the owner can add new subscribers. The messages can be interchanged between any combination of the added subscribers.

</br>
</br>
<p align="center">
  <img src="https://github.com/janishar/janishar.github.io/blob/master/images/jpost_viz.png" width="400">
  <h4 align="center">Communication Model</h4>
</p>
</br>
</br>
<p align="center">
  <img src="https://github.com/janishar/janishar.github.io/blob/master/images/jpost_exe_viz.png" width="750">
  <h4 align="center">Execution Model</h4>
</p>
</br>
</br>
<hr />

## If this library helps you in anyway, show your love :heart: by putting a :star: on this project :v:

# Gradle
# Java
```groovy
dependencies {
    compile 'com.mindorks:java-jpost:0.0.3'
}
```
# Android
```groovy
dependencies {
    compile 'com.mindorks:android-jpost:0.0.3'
}
```

# Why should you use this library
1. In contrast to the existing pub-sub libraries, it hold the subscribers with weakreference. Thus it doesn't create memory leaks.
2. Single message can be sent to selected subscribes. This avoids the problem of event getting received at undesirable places. Thus minimising the chances of abnormal application behaviour.
3. The subscriber addition can be controlled by using private channels. It minimises the chances of adding subscribes by mistake to receive undesirable messages.
4. It is a tiny library < 55kb . Thus not effecting the application overall size.
5. It facilicates synchronous as well as asynchronous message delivery and processing.
6. It provides a mechanism to run code asynchronously.

# Library Classes Overview
# `JPost`
### The one point entry for this library is the class `JPost`. It contains static methods to access `BroadcastCenter` class and core functionality related to the library management.
## Methods
1. `JPost.getBroadcastCenter()`: This method is used to get the instance of BroadcastCenter class (BroadcastCenter is described below)
2. `JPost.shutdown()`: This method closes the `JPost` for the async operations and removes all the pool threads. It should called when the program terminates or as required. This call let the message delivery already in process to continue.
3. `JPost.haltAndShutdown()`: This method does the same operation as do the `JPost.shutdown()` but it also removes all the message delivery tasks.;

<hr />

# `BroadcastCenter`
### This class proivdes all the functionality attached with this library. 
## Methods
1. `createPrivateChannel(T owner, Integer channelId)`: Creates a private channel requiring a unique int channel id. The owner is assigned `owner.hashCode()` as subscriber id.
2. `createPrivateChannel(T owner, Integer channelId, Integer subscriberId)`: Creates a private channel requiring a unique int channel id. The owner is given `subscriberId` as subscriber id.
3. `createPublicChannel(Integer channelId)`: Creates a public channel requiring a unique int channel id.
4. `stopChannel(Integer channelId)`: Stops the channel with channel id temporarily.
5. `reopenChannel(Integer channelId)`: Reopen the channel which has been stopped but not terminated.
6. `terminateChannel(Integer channelId)`: Completely removes the channel and can not be used later.
7. `getChannel(Integer channelId)`: Return the channel with channel id else returns null.
8. `broadcast(T msg)`: This method sends messages to all the subscribers of the default global channel.
9. `broadcastAsync(T msg)`: This method sends messages asynchronously to all the subscribers of the default global channel. The thread calling this methods can process with remaining tasks as usual.
10. `broadcast(Integer channelId, T msg, Integer... subscribers)`: This method sends messages to the subscribers of the channel of a particular channel id. If subscribers is not provided then all the subscribers of this channel receiver the message.
11. `broadcastAsync(Integer channelId, T msg, Integer... subscribers)`: Does the same as above(method 10) but asynchronously.
12. `broadcast(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers)`: This method is used to send message on a private channel. Only the registered subscribers can send and receive messges on private channel.
13. `broadcastAsync(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers)`:  Does the same as above(method 12) but asynchronously.
14. `addSubscriber(T subscriber)`: This method add subscribers to default global channel.
15. `addSubscriber(Integer channelId, T subscriber)`: This method add subscribers to public channels with subscriber having `subscriber.hashCode()` as the subscriber id.
16. `addSubscriberAsync(Integer channelId, T subscriber)`: Does the same as above(method 15) but asynchronously.
17. `addSubscriber(Integer channelId, T subscriber, Integer subscriberId)`: This method add subscribers to public channels with subscriber having `subscriberId` as the subscriber id.
18. `addSubscriberAsync(Integer channelId, T subscriber, Integer subscriberId)`: Does the same as above(method 17) but asynchronously.
19. `addSubscriber(V owner, Integer channelId, T subscriber)`: This method add subscribers to private channels. Only owner of the channel can add subscribers to this channel. The subscriber is given subscriber.hashCode() as the subscriber id.
20. `addSubscriberAsync(V owner, Integer channelId, T subscriber)`: Does the same as above(method 19) but asynchronously.
21. `addSubscriber(V owner, Integer channelId, T subscriber, Integer subscriberId)`: This method add subscribers to private channels. Only owner of the channel can add subscribers to this channel. The subscriber is given `subscriberId` as the subscriber id.
22. `addSubscriberAsync(V owner, Integer channelId, T subscriber, Integer subscriberId)`: Does the same as above(method 21) but asynchronously.
23. `removeSubscriber(T subscriber)`: Removes subscriber form the default global channel.
24. `removeSubscriber(Integer channelId, T subscriber)`: Removes subscriber form a public channel.
25. `removeSubscriber(T registeredSubscriber, Integer channelId, Integer subscriberId)`: Removes subscriber form a private channel. Only registered subscribers of the private channel can remove a subscriber.
26. `getAllSubscribersWeakRef()`: returns a collection of weakreference holding the subscriber. 

<hr />

# Receiving Messages
### The messages are received via `@OnMessage` method annotation. The class subscribing any message has to create a method with message object as the parameter and annotate it with `@OnMessage`.

### `@OnMessage` Parameters
1. `channelId`: This parameter in the annotation, attaches the message reception to a particular channel on which the class has subscribed. If not provided will be listening to the default global channel.
2. `isCommonReceiver`: This parameter sets the message reception from any channel on which the class has subscribed.

# Android Note:
### `@OnUiThread` : This annotation run the code in UI / main thread
```java
    @OnUiThread
    @OnMessage
    private void onMessage(final Message msg){
        textView.setText(msg.getMsg());
    }
```

# Proguard Note:
### If you are using proguard, then add this rule in proguard-project.txt
```groovy
  -keepattributes *Annotation*
  -keepclassmembers class ** {
    @com.mindorks.jpost.core.OnMessage <methods>;
    @com.mindorks.androidjpost.droid.OnUiThread <methods>;
  }
```

## Example 1: Sending and receiving messages over default global channel

### Step 1: Create a message class that will be passed on the channel for broadcasting.

```java
   public class Message1 {

     private String msg;

     public Message1(String msg) {
          this.msg = msg;
     }

     public String getMsg() {
         return "Message1: " + msg;
     }

     public void setMsg(String msg) {
          this.msg = msg;
      }
   }
```
### Step 2: Subscribe the class to the default global channel
### Step 3: Attach the message to a recipient class

```java
    public SubscriberA() {
        try {
            JPost.getBroadcastCenter().addSubscriber(this);
        }catch (AlreadyExistsException | NullObjectException e){
            e.printStackTrace();
        }
    }
    
    @OnMessage
    private void onMessage1(Message1 msg){
        System.out.println("SubscriberA: "+ msg.getMsg());
    }
```

### Step 4: Send the message to the class
```java
   //TO SEND THROUGH THE CLASS RUNNING THREAD USER
   JPost.getBroadcastCenter().broadcast(new Message1("Application sending message"));
   
   // TO SEND ASYNCHRONOUSLY
   try {
        JPost.getBroadcastCenter().broadcastAsync(new Message2("Application sending message"));
    }catch (JPostNotRunningException e){
        e.printStackTrace();
    }
```

## Example 2: Creating public channel and sending and receiving messages over it

### Step 1: Create Message class. This is same as above expamle 1.

### Step 2: Create a public channel and give it a unique id.

```java
    class ChannelIds{
        public static final int publicChannel1 = 1;
    }
    
    .....
    try {
        JPost.getBroadcastCenter().createPublicChannel(ChannelIds.publicChannel1);
    }catch (AlreadyExistsException e){
        e.printStackTrace();
    }
```
### Step 3: Subscribe and attach a message recipient to a class
```java
     public SubscriberA() {
        // TO ADD SUBSCRIBER SYNCHRONOUSLY 
        try {
            JPost.getBroadcastCenter().addSubscriber(ChannelIds.publicChannel1, this);
        }catch (PermissionException | NoSuchChannelException | AlreadyExistsException 
                  | IllegalChannelStateException | NullObjectException e){
            e.printStackTrace();
        }
        
        ...
        // TO ADD SUBSCRIBER ASYNCHRONOUSLY 
        // JPost.getBroadcastCenter().addSubscriberAsync(ChannelIds.publicChannel1, this);
    }
    
    @OnMessage(channelId = ChannelIds.publicChannel1)
    private void onMessage1(Message1 msg){
        System.out.println("SubscriberA: " + msg.getMsg());
    }
```

### Step 4: Send Message via public channel
```java
    //TO SEND THROUGH THE CLASS RUNNING THREAD USER
    JPost.getBroadcastCenter().broadcast(ChannelIds.publicChannel1, new Message1("Application sending public message"));
  
    // TO SEND ASYNCHRONOUSLY
    try {
        JPost.getBroadcastCenter().broadcastAsync(ChannelIds.publicChannel1, new Message1("Application sending public async message"));
    }catch (JPostNotRunningException e){
        e.printStackTrace();
    }
```

## Example 3: Creating private channel, adding subscribers(Only creator/owner of the channel has adding subscribers right) and sending and receiving messages over it
```java
    public class ChannelIds {
        public static final int privateChannel1 = 2;
    }
    
    // CREATING PRIVATE CHANNEL
    try {
        JPost.getBroadcastCenter().createPrivateChannel(this, ChannelIds.privateChannel1);
    }catch (AlreadyExistsException e){
        e.printStackTrace();
    }
    
    // ADDING 
    try {
        JPost.getBroadcastCenter().addSubscriber(this, ChannelIds.privateChannel1, subscriberA);
    }catch (PermissionException | NoSuchChannelException | AlreadyExistsException 
                  | IllegalChannelStateException | NullObjectException e){
        e.printStackTrace();
    }
    
    // SUBSCRIBING PRIVATE MESSAGES
    @OnMessage(channelId = ChannelIds.privateChannel1)
    private void onMessage1(Message1 msg){
        System.out.println("SubscriberD: " + msg.getMsg());
    }
    
```

# Interesting Usage
### 1. The subscribers are added with unique ids. If id is not provided then its hashcode is taken as the id.
```java
    int subscriberId = 1;
    try {
        JPost.getBroadcastCenter().addSubscriber(ChannelIds.publicChannel1, this, subscriberId);
    }catch (PermissionException | NoSuchChannelException | AlreadyExistsException 
                | IllegalChannelStateException | NullObjectException e){
        e.printStackTrace();
    }
```
### 2. The message can also be send to select subscribers on the channel
```java
    int subscriberId1 = 1;
    int subscriberId2 = 2;
    try {
        JPost.getBroadcastCenter().broadcastAsync(ChannelIds.publicChannel1, 
                new Message1("Application sending public message"), subscriberId1, subscriberId2);
    }catch (JPostNotRunningException e){
        e.printStackTrace();
    }
```

# Recent Library: [`PlaceHolderView`](https://github.com/janishar/PlaceHolderView)
#### `PlaceHolderView` create views without any adapter in very modular form. It uses the power of RecyclerView and enhances it to another level. For the first time with the list view comes card stack view.

# License

```
   Copyright (C) 2016 Janishar Ali Anwar

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License

```




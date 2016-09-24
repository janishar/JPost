# JPost (Java class communication library)
#Intro
###This library is designed for the communication between classes in java by sending and receiving messages. Messages can be any object.
The design of this library is such that the modularity of the code is enhanced and provide a controlled system for sending and receiving messages. One of the key advantages of this library is that the message handling can be done both synchronusly(if the sender thread is same as the receiver thread) or asynchronously as provided. All the subscribing classes are holded with weakreferences, hense the memory leak do not take place. The usecases in which this library's power can be understood is when compared to other pub/sub libraries. Situations in which many instances of a single class requrire to process messages based on the sender. Example: A class wants to send message to few of the instances of the same class. The schema diagram provided below will provide a better insight.
<hr />

#Library Design Overview
##The channel through which communication can be done is categorised into three categories:
1. **Default Channel**: This is the prebuilt channel and allowes global communication. When subscribed to this channel, the class can send messages to all the subscribed classes with message type, on this channel.
2. **Public Channel**: This channel is designed for filtered communication. Public channels are required to be created and the subscribers of this channel can receive messages broadcasted to this channel. The messaged can also be send to selected subscribes.
3. **Private Channel**: This channel is designed to control the access to the channel. The private channels need to be created and stores the owner information. Only the owner can add new subscribers. The messages can be interchanged between any combination of the added subscribers.

</br>
</br>
<p align="center">
  <img src="https://github.com/janishar/janishar.github.io/blob/master/images/jpost_viz.png" width="400">
  <h3 align="center">Communication Model</h1>
</p>
</br>
</br>
<p align="center">
  <img src="https://github.com/janishar/janishar.github.io/blob/master/images/jpost_exe_viz.png" width="750">
  <h3 align="center">Execution Model</h1>
</p>
</br>
</br>
<hr />

#Library Classes Overview
#JPost
##The one point entry for this library is the class JPost. It contains static methods to access BroadcastCenter class and core functionality related to the library management.
##Methods
1. **JPost.getBroadcastCenter()**: This method is used to get the instance of BroadcastCenter class (BroadcastCenter is described below)
2. **JPost.shutdown()**: This method closes the JPost for the async operations and removes all the pool threads. It should called when the program terminates or as required. This call let the message delivery already in process to continue.
3. **JPost.haltAndShutdown()**: This method does the same operation as do the **_JPost.shutdown()_** but it also removes all the message delivery tasks.;

<hr />

#BroadcastCenter
## This class proivdes all the functionality attached with this library. 
##Methods
1. **createPrivateChannel(T owner, Integer channelId)**: Creates a private channel requiring a unique int channel id. The owner is assigned owner.hashCode() as subscriber id.
2. **createPrivateChannel(T owner, Integer channelId, Integer subscriberId)**: Creates a private channel requiring a unique int channel id. The owner is subscriberId as subscriber id.
3. **createPublicChannel(Integer channelId)**: Creates a public channel requiring a unique int channel id.
4. **stopChannel(Integer channelId)**: Stops the channel with channel id temporarily.
5. **reopenChannel(Integer channelId)**: Reopen the channel which has been stopped but not terminated.
6. **terminateChannel(Integer channelId)**: Completely removes the channel and can not be used later.
7. **getChannel(Integer channelId)**: Return the channel with channel id else returns null.
8. **broadcast(T msg)**: This method sends messages to all the subscribers of the default global channel.
9. **broadcastAsync(T msg)**: This method sends messages asynchronously to all the subscribers of the default global channel. The thread calling this methods can process with remaining processing as usual.
10. **broadcast(Integer channelId, T msg, Integer... subscribers)**: This method sends messages to the subscribers of the channel of a particular channel id. If subscribers is not provided then all the subscribers of this channel receiver the message.
11. **broadcastAsync(Integer channelId, T msg, Integer... subscribers)**: Does the same as above(method 10) but asynchronously.
12. **broadcast(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers)**: This method is used to send message on a private channel. Only the registered subscribers can send and receive messges on private channel.
13. **broadcastAsync(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers)**:  Does the same as above(method 12) but asynchronously.
14. **addSubscriber(T subscriber)**: This method add subscribers to default global channel channels.
15. **void addSubscriber(Integer channelId, T subscriber)**: This method add subscribers to public channels with subscriber having subscriber.hashCode() as the subscriber id.
16. **addSubscriberAsync(Integer channelId, T subscriber)**: Does the same as above(method 15) but asynchronously.
17. **addSubscriber(Integer channelId, T subscriber, Integer subscriberId)**: This method add subscribers to public channels with subscriber having subscriberId as the subscriber id.
18. **addSubscriberAsync(Integer channelId, T subscriber, Integer subscriberId)**: Does the same as above(method 17) but asynchronously.
19. **addSubscriber(V owner, Integer channelId, T subscriber)**: This method add subscribers to private channels. Only owner of the channel can add subscribers to this channel. The subscriber is given subscriber.hashCode() as the subscriber id.
20. **addSubscriberAsync(V owner, Integer channelId, T subscriber)**: Does the same as above(method 19) but asynchronously.
21. **addSubscriber(V owner, Integer channelId, T subscriber, Integer subscriberId)**: This method add subscribers to private channels. Only owner of the channel can add subscribers to this channel. The subscriber is given subscriberId as the subscriber id.
22. **addSubscriberAsync(V owner, Integer channelId, T subscriber, Integer subscriberId)**: Does the same as above(method 21) but asynchronously.
23. **removeSubscriber(T subscriber)**: Removes subscriber form the default global channel.
24. **removeSubscriber(Integer channelId, T subscriber)**: Removes subscriber form a public channel.
25. **removeSubscriber(T registeredSubscriber, Integer channelId, Integer subscriberId)**: Removes subscriber form a private channel. Only registered subscribers of the private channel can remove a subscriber.
26. **getAllSubscribersWeakRef()**: returns a collection of weakreference holding the subscriber. 

#If this library helps you in anyway, show your love :heart: by putting a :star: on this project :v:

#Gradle
```java
dependencies {
    compile 'com.mindorks:jpost:1.0.0-snapshot'
}
```
#License

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




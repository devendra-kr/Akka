# Akka
Akka is a suite of modules which allows you to build distributed and reliable systems by leaning on the actor model. The actor model puts emphasis on avoiding the use of locks in your system, in favour of parallelism and concurrency. As a result, actors are those 'things' that would 'react' to messages, and perhaps run some computation and/or respond to another actor via message passing. The actor model has been around for a while, and was certainly made popular by languages such as Erlang. 

Akka brings similar features around concurrency and parallelism onto the JVM, and you can use either Java or Scala with the Akka libraries. Without any surprises, our Akka code snippets below will be making use of Scala. The Akka eco-system has evolved fairly rapidly over the past few years. Below is a quick overview of the various Akka modules for which we will provide code snippets:

1. Akka Actor: This module introduces the Actor System, functions and utilities that Akka provides to support the actor model and message passing. 

2. Akka HTTP: As the name implies, this module is typically best suited for middle-tier applications which require an HTTP endpoint. As an example, you could use Akka HTTP to expose a REST endpoint that interfaces with a storage layer such as a database. 

3. Akka Streams: This module is useful when you are working on data pipelines or even stream processing. 

4. Akka Networking: This module provides the foundation for having actor systems being able to connect to each other remotely over some predefined network transport such as TCP.  

5. Akka Clustering: This module is an extension of the Akka Networking module. It is useful in scaling distributed applications by have actors form a quorum and work together by some predefined membership protocol.


instance of akka.actor.ActorSystem. have a class extend the Actor trait

Actor System (ActorSystemName) :  

terminate() : Close the actor system.

receive() :  The receive method is the place where you instruct your actor which messages or protocols it is designed to react to.

actorOf() : 

Props : 

Tell Pattern OR ! : This pattern is useful when you need to send a message to an actor, but do not expect to receive a response. "fire and forget"

Ask Pattern OR ? : This pattern allows you to send a message to an actor, and get a response back.

sender : 

asynchronous non-blocking : 

PipeTo(sender) :


Actor hierarchy : 
							Root guardian (/)
					 		/		    \		
						   /			 \
					User guardian(/user)  System guardian(/system)
				  		  /				   \
system.actorOf() -->  /user/someActor    /system/someInternalActor
						  |
context.someActor() --> /user/someActor/someChild

Actor Lookup : system.actorSelection 

Child Actors : 

Actor Lifecycle : 

akka.actor.PoisonPill :	which is a special message that you send to terminate or stop an actor. When the PoisonPill message has been received by the actor, you will be able to see the actor stop event being triggered from our log messages.

NOTE:
1. Instead of blocking our main program using Await.result(), we will make use of Future.onComplete() callback to capture the result of a Future
2. ActorRef, we can then use the bang operator ! to send a message to it using the Tell Pattern.

## PLAY FRAMEWORK - APPLICATION EXAMPLE

Small application with a few examples of Play Framework features.
Asynchronous request processing and how to do then your are async but have to make some blocking-method invocation are in focus.

### How to start

In order to start application you need to have Java 8 and Activator installed.
More info here: [Installing Play](https://www.playframework.com/documentation/2.5.x/Installing)

### What can I find here?

Routes file is a heart of Play Framework (demo-play/conf/routes), you should start from here.
There you can find 3 main example of how to use Play
 - /simple/* demo-play/app/controllers/SimpleController - set of simple request processing examples
 - /lastfm/* demo-play/app/controllers/LastFmController - examples of async Play WebService Library usage.
 - /messenger demo-play/app/controllers/MessengerController - simple chat example with Actors usage and blocking db-operations

### You can also check
 - demo-play/app/filters/CustomGzipFilters - filter which zip all your html responses
 - demo-play/app/views/messenger - Twirl template engine example (part of messenger example)
 - demo-play/app/models/MessageEntity - EBean ORM usage example (part of messenger example)
 - demo-play/app/Module - place where Guice Dependency Injection  beans are configured
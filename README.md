
## About :thought_balloon:
This is a todo list app that demonstrate the use of modern android architecture component with MVVM Architecture ,  i focused on learning flow with other architecture components and i wrote a short simple notes about about what i learned in this repo.

## Built with  :wrench:
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - LiveData is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware, meaning it respects the lifecycle of other app components.
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
- [Jetpack Navigation](https://developer.android.com/guide/navigation) - Navigation refers to the interactions that allow users to navigate across, into, and back out from the different pieces of content within your app
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed objects with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data asynchronously, consistently, and transactionally.
- [Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - An asynchronous data stream that sequentially emits values and completes normally or with an exception.
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously.
- [Room](https://developer.android.com/topic/libraries/architecture/room) - SQLite object mapping library.
- [Hilt](https://dagger.dev/hilt/) - Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.

## Flow 
![App Screenshot](https://developer.android.com/static/images/kotlin/flow/flow-entities.png)
### Hot stream vs Cold stream
Hot streams used to send data even if there's no subscribers , it does not care if there isn't anybody listening to it.
If any subscriber start to listen the data will not produced again.

Cold streams is unlike hot streams it needs a subscriber to start emitting the values of the stream.

Flow is a cold stream and to create it we need a builder.
`val emitter = flow { emit(...) }`

flow is an interface that have a method to collect the stream of values.
```
public interface Flow<out T> {
    public suspend fun collect(collector: FlowCollector<T>)
}
```
Flow is like rxjava also has operators like map, filter, reduce and these operators supports suspending functions on most operators that's mean we can do sequential asynchronous tasks.


### StateFlow 
we used to use live data to save states because it is lifecycle aware and only start emitting values when LifecycleOwner is on active state also StateFlow can be lifecycle aware by using `viewLifecyleOwner.lifecycleScope` + `launchWhenStarted` .

The BAD thing about live data that is work on the main thread.
## Channels One-Time Events
Channels used to establish a connection between a sender and a receiver it is similar to reactive streams.
Channels basically have a capacity or a buffer contain the elements that will be emitting to the consumer.

`public fun <E> Channel(capacity: Int = RENDEZVOUS): Channel<E>
`
![App Screenshot](https://miro.medium.com/max/1400/1*YTfrJ1fNEiovCvmvNnjb8g.png)

that's mean that the sender will send the elements untill the channel reach out the maximum number of elements that a channel can contain in a buffer, after that the sender will stop untill the consumer/receiver read the data from the channel.
Since the RENDEZVOUS = 0 that's mean there's no buffer



From Roman Elizarov article has described single-shot event bus that is a buffered stream of events by using channels to handle events that must be processed exactly once. This happens in a design with a type of event that usually has a single subscriber, but intermittently (at startup or during some kind of reconfiguration) there are no subscribers at all, and there is a requirement that all posted events must be retained until a subscriber appears.

___________________________________________________________________
___________________________________________________________________

## fallbackToDestructiveMigration() 
In room if we change our database scheme but we did not upgrade the version of the database OR if we really upgrade the version but we did not provide migration the app will crash anyway.
Migration is a class defines what should happen when migrating from version to another version.
`fallbackToDestructiveMigration()` used when we don't want any migration or when we wanna the database to be cleared after upgrading the version.

___________________________________________________________________
___________________________________________________________________

## ListAdapter
Since `notifyDataSetChanged()` redraws the entire view which an expensive operation even if there is items never changes in the recycler view , the list adapter was an amazing solution to handle all changes that happen to our recycler view.

The list adapter knows how to redraws the view that are added and removed and ouput a list with the new changes by using "DiffUtil" .

___________________________________________________________________
___________________________________________________________________

## DataStore

![App Screenshot](https://3.bp.blogspot.com/-Vk_q5hWw6DQ/X00ZfiRrB9I/AAAAAAAAPlo/u-kvBvmMfzgRnNViYLwaAim-E7wq5yxKACLcBGAsYHQ/s1600/Screen%2BShot%2B2020-08-31%2Bat%2B11.25.43%2BAM.png)

* In async api the shared preferences uses `OnSharedPreferenceChangeListener` to handle changes values which invoked on the main thread.

* In shared preferences `apply()` made work in background but it block the ui thread causing jank.


# ZayES-Bullet

ZayES and Bullet are two key elements of jME3. This library aims to be
a flexible and easy to use adapter between them. Every entity could be
integrated into the physics calculations by only combining physics 
components. 

## Getting started

The easiest way to get started is to clone this repository
and have a look at the examples in the test folder. You'll
notice the most important aspects.

## Adding ZayEs-Bullet to your project

ZayES-Bullet is released on jcenter. Just add the dependency definition
to your build file or download directly the needed jars. There are
javadoc and sources available. 
([Direct link to jcenter](https://bintray.com/jvpichowski/jME3-Tools/ZayES-Bullet))

## Architecture

ZayES-Bullet consists of two parts. On the one hand there are the
components you need to describe your entity (e.g. mass, shape, ...).
Have a look in the `components` package to discover all of them.
On the other hand there is the BulletSystem which pulls those information,
adds them to the physics simulation and propagates the new state back to
the entity in form of components. Then you could watch for them and
change your game view. The BulletSystem is the complex part and could
be extended in various ways but usually you only have to create it and
call `update()` from time to time to update its state. Don't forget to
destroy it if you don't need it anymore. I have added an `ESBulletState` 
Because this library is mostly used by jME3 developers. This states
handles the creation, destruction and update of the BulletSystem for you.
You simple have to attach it to your StateManager. In the constructor
you can specify different threading modes. See the javadoc for more
explanation on them.

## Thread safety

ZayES is by default threadsafe. As a consequence you don't have to worry to much
about thread safety if you only read and add components. But there could
be some pitfalls with the BulletSystem for advanced users because you can 
obtain direct access to the Bullet PhysicsSpace (e.g. to register listeners). You have to be aware
that the listeners are called from the physics thread and not from the
render thread. As a consequence you have to enqueue all events.

## Versioning

This library is still in an alpha state. The key features should work but
there has to be more testing. Be aware that the api might change. At the
moment I'm very happy with the core api and it probably won't change anymore.
But be warned that there could still be breaking changes in later releases.

## Road map

The next step will be a good character control. Then I will add more
advanced components like a force field. And I will improve my own debug
view.

## License

ZayES-Bullet is released under the BSD 3-Clause License. If you like it
I would be proud if you show me your work and give credit to me.

## Getting in touch

I have a [thread in the jME3 forum](https://hub.jmonkeyengine.org/t/bullet-zay-es-example/37946)
where I will discuss several aspects. There you have the chance to ask 
questions and state your requirements.
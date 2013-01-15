# embellir hacker's guide

embellir: *making beautiful that which you want to know*

This is a hacker's guide to embellir. It intends to give you a quick
understanding of the code so you can extend embellir.

## The Model

There are three major components to embellir: the Illustrator, the Curator, and
the Bit Dock.

The Illustrator manages a collection of doodles, which are functions to draw
something on the screen using Quil. For example, a simple doodle might just
write the current time to the screen.

The Curator manages a collection of curios, which are bundles of data along
with functions to manage that data. The current weather for your area might be
such a curio.

The Bit Dock handles TCP connections. A simple protocol allows for both feeding
data to curios and directing the display of doodles.

![a simple diagram of the embellir model](...)

### Doodles

The Illustrator is an entity-component system. The doodles, however, are just
functions which draw upon a designated screen area. The only important thing to
know is that drawing function will have all the entity data passed to it. Your
drawing should assume it has already been (translated) into place. The entity
data will contain a component describing the boundary within which it should
draw.

	(defn draw-something [entity]
		(let [boundary (:bound entity)
			  width (:width boundary)
			  height (:height boundary)]
			  ... draw things here ...
			  ))


### Curios

The Curator keeps a list of curios, any of which can be queried by using
(get-curio "curioname"). If the curio needs to be updated, it should provide a
'time-to-live' and the Curator will call the update fuction every time that
period comes up.

Keeping curio data in a map is highly suggested, as the bit dock will only
handle data in that format.


### Bit Dock

See the main README.md for samples of the protocol. The only thing to really
know is: the bit dock will try to call the receive function of a curio when it
receives the SUPPLY command. If the data after the curio name cannot be read
into a map structure by the clojure reader, it will be rejected.


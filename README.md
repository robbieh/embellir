# embellir

embellir: *making beautiful that which you want to know*


## Usage

    $ java -jar embellir-0.1.0-standalone.jar [args]

Once running, embellir listens on port 9999. You can connect to that port and
issue commands as detailed in the protocol section below.

## Options

You can pass the name of an rc file on the command line. It is the same format
as the network protocol.  A simple one would look like this:

	ILLUSTRATE polarclock
	LAYOUT layout-major-central

Imagine you name it embellir.rc. You'd then start embellier like so:
    
	$ java -jar embellir-0.1.0-standalone.jar ./embellir.rc

## Protocol

A quick list of samples:

	LAYOUT layout-tiled
	CURATE weather cpu ...
	ILLUSTRATE polarclock cpu weather ...
	SUPPLY curioname {:keyword "test" :text "this is a test\nmore test\nmore test"}

## Aspirations

Maps tell you where you are in space.

Clocks tell you where you are in time.

Though this is just a toy project so I can learn more Clojure, I would like for it simply tell you where you are.



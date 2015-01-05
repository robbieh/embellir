# embellir

embellir: *making beautiful that which you want to know*


## Usage

    $ java -jar embellir-0.1.2-standalone.jar 

Once running, embellir listens on port 9999. You can connect to that port and
issue commands as detailed in the protocol section below.

## Configuration


### $HOME/.embellir.rc

### $HOME/.embellir.startup

### $HOME/.embellir/embellir


## Protocol

A quick list of samples:

	LAYOUT layout-tiled
	CURATE weather 
	CURATE cpu
	ILLUSTRATE polarclock 
	ILLUSTRATE cpu 
	ILLUSTRATE weather 
	SUPPLY curioname {:keyword "test" :text "this is a test\nmore test\nmore test"}

## Aspirations

Maps tell you where you are in space.

Clocks tell you where you are in time.

I would like for embellir to simply tell you where you are.



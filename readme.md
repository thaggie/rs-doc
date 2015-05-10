#rs-doc

A project to build simple documenation straight from jax-rs annotations.

##Projects

* rs-doc-annotations - Annotation classes to give a bit more info for the documentation.
* rs-doc-builder - Builder that will take a jar and generate documentation for it.
* rs-doc-example - An example project with a simple resource that can be documented

##Building

`gradle jar`

##Running the example (after building).

Arguments:

* The jar file with the endpoints
* The directory to write the api.md file to

`java -jar rs-doc-builder/build/libs/rs-doc-builder.jar rs-doc-example/build/libs/rs-doc-example.jar .`

Creates [api.md](api.md)

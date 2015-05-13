#rs-doc

A project to build simple documenation straight from jax-rs annotations.

##Projects

* rs-doc-annotations - Annotation classes to give a bit more info for the documentation.
* rs-doc-builder - Builder that will take a jar and generate documentation for it.
* rs-doc-example - An example project with a simple resource that can be documented

##Usage

For the most part it just using the [jax-rs](https://jax-rs-spec.java.net/) annotations will produce a description of the API to add commentary, use the `@Doc` annotation to add descriptions.

```
@Path("/bar/{id}")
@GET
@Doc("This method really raises the bar.")
@Produces("application/json")
public Bar bar(
		@Doc("The identifier of the thing that's used to do the thing.") @PathParam("id") String id , 
		@Doc("The name of the baz.") @QueryParam("baz") String baz
		) {
	return new Bar(id, baz);
}
```

When using an asynchronous response (`@Suspended AsyncResponse ar`) you will also need to add an annotation to the method to indicate the response type: `@ResponseType(Bar.class)`

##Sample Output

`GET /foo/bar/{id}`

This method really raises the bar.

Query:

* **baz** (String) - The name of the baz.

`application/json`

```
{
  "id" : null,
  "baz" : null
}
```

##Building

`gradle jar`

##Running the example (after building).

Arguments:

* The jar file with the endpoints
* The directory to write the api.md file to

`java -jar rs-doc-builder/build/libs/rs-doc-builder.jar rs-doc-example/build/libs/rs-doc-example.jar .`

Creates [api.md](api.md)

##License

[Apache 2](www.apache.org/licenses/LICENSE-2.0)
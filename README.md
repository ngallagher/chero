## **THIS DOCUMENT IS A WORK IN PROGRESS**

This provides a basic template for a service. It includes dependency injection and a web framework with
support for HTTP and WebSocket communication. The dependency injection system is built on the 
ultra fast [Class Graph](https://github.com/classgraph/classgraph) project and the web 
framework has a close resemblance to [JAX-RS](https://github.com/jax-rs/api).

### Overview

This framework forms the basis of a lightweight ultra fast dependency injection system. It has been
largely inspired by [Spring Boot](https://github.com/spring-projects/spring-boot) 
and [JAX-RS](https://github.com/jax-rs/api). Rather than expanding on the concepts and functionality
available in these well know libraries, this framework offers a reduced set of features in order
to provide a much faster and more deterministic dependency injection system. The result is a simpler
more approachable route to building your micro-services.

The framework is built around the concept of modules and components. A module is a class with the @Module 
annotation. It forms the basis of the dependency injection
framework by constraining the scope of the application. Scoping restricts the loading of components to
the module package and its children. In addition to
applying these constraints they act as component providers through methods declared with the 
@Provides annotation. All other components are those classes within the module scope declared with 
the @Component annotation. The @Import annotation allows a module to expand the application scope
by including a dependent module and its components.

```java
@Module
@Import(MailModule.class)
public class LoginModule {

	@Provides
	public LoginService loginService() {
		// ...
	}
}

@Module
public class MailModule {


	@Provides
	public MailClient mailClient() {
		// ...
	}
}

@Component
public class MailService {

	private final MailClient client;
	
	public MailService(MailClient client) {
		this.client = client;
	}
}
```

#### Core Framework

The annotations for the core framework

| Annotation      | Description   | 
| ------------- | ------------- | 
| @Module       |               |
| @Import       |               |
| @Provides       |               |
| @Component       |               |
| @Value       |               |
| @DefaultValue       |               |
| @Inject       |               |
| @Require       |               |
| @When       |               |
| @WhenExists       |               |
| @Unless       |               |
| @UnlessExists       |               |

A complementary library is provided to facilitate exposing functionality though HTTP and WebSockets. This
framework is largely inspired by [JAX-RS](https://github.com/jax-rs/api) but does not follow the 
official specification as this limits the usefulness and functionality of the library. Below is an example
of how you would declare a resource.

```java
@Path("/login")
public class LoginResource {

	private final LoginService service;
	
	public LoginResource(LoginService service) {
		this.service = service;
	}

	@POST
	@Path("/register/{type}")
	@Produces("application/json")
	public AccessRequest register(
		@PathParam("type") UserType type,
		@QueryParam("user") String user,
		@QueryParam("email") String email,
		@QueryParam("password") String password)
	{
		return service.register(type, user, email, password); 
	}
	
	@POST
	@Path("/grant")
	public AccessGrant grant(
		@QueryParam("user") String user,
		@QueryParam("token") String token)
	{
		return service.grant(user, token);
	}
}

```

#### Web Framework

The annotations for the core framework

| Annotation      | Description   | 
| ------------- | ------------- | 
| @GET       |               |
| @POST       |               |
| @PUT       |               |
| @DELETE       |               |
| @PATCH       |               |
| @CONNECT       |               |
| @Path       |               |
| @PathParam       |               |
| @QueryParam       |               |
| @HeaderParam       |               |
| @CookieParam       |               |
| @Attribute       |               |
| @Consumes       |               |
| @Produces       |               |
| @CacheControl       |               |
| @Attachment       |               |
| @Intercept       |               |
| @Subscribe      |               |
| @Entity        |               |

```java
@Module
public class DemoApplication {
   
   @Path
   public static class DemoResource {
      
      @Value("${message}")
      private String text;
      
      @GET
      @Path("/.*")
      @Produces("text/plain")
      public CompletableFuture<ResponseEntity> helloWorld() {
         return CompletableFuture.supplyAsync(() -> ResponseEntity.create(Status.OK)
            .type("text/plain")
            .cookie("TEST", "123")
            .entity(text)
            .create()
         );
      }
   }
   
   public static void main(String[] list) throws Exception {
      Application.create(ServerDriver.class)
         .path("..")
         .module(DemoApplication.class)
         .create(list)
         .name("Apache/2.2.14")
         .session("SESSIONID")
         .threads(10)
         .start()
         .bind(8787);
   }
}
```

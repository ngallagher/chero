## **WORK IN PROGRESS**

This provides a basic template for a service. It includes dependency injection and a web framework with
support for HTTP and WebSocket communication. The dependency injection system is built on the 
ultra fast [Class Graph](https://github.com/classgraph/classgraph) project and the web 
framework has a close resemblance to [JAX-RS](https://github.com/jax-rs/api).

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

A module is a class with the ```@Module``` annotation. They form the basis of the dependency injection
framework by constraining the components that form the basis of the application. The basic constraint is
that no component or module may exist outside the package the module it is declared in. In addition to
applying constraints to the scope of the application that act as factories that may provide components
via methods with the ```@Provides``` annotation. All other components are those classes with the ```@Component```
annotation.

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
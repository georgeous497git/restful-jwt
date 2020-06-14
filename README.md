![Presentacion](https://github.com/georgeous497git/restful-jwt/blob/master/img/miniatura.png)

# Web Service RESTful con Spring Boot y JWT
En este mini-tutorial basados en un proyecto Spring Boot expondr&eacute; un Web Service Rest utilizando la librer&iacute;a JWT (JSON Web Token).

## Resultado esperado
Al finalizar el tutorial tendr&aacute;s como resultado un Web Service RESTful expuesto desde el servidor local de tu computadora y que podr&aacute; ser consumido por un cliente REST conectado a tu misma red y conocer el comportamiento de Tokens JWT.

* Insumos
  * IDE Spring Tool Suite 4
  * Spring Boot 2.2.6
  * Maven 3.6.3
  * Java JDK 1.8.0_241
  * Java JWT 3.10.3
  * Postman 7.25.0


## 1. Inicio e Instalaci&oacute;n

**a)** Creaci&oacute;n de proyecto Spring Boot<br>
![Genera Spring Boot](https://github.com/georgeous497git/restful-jwt/blob/master/img/01_creacion.png)
__________________________________________________________________
**b)** Configuraci&oacute;n de paquetes proyecto Spring Boot<br>
![Configura Spring Boot](https://github.com/georgeous497git/restful-jwt/blob/master/img/02_package.png)
__________________________________________________________________
**c)** Selecci&oacute;n de *Starter Web* proyecto Spring Boot<br>
![Starter Spring Boot](https://github.com/georgeous497git/restful-jwt/blob/master/img/starter_web.png)
__________________________________________________________________
**d)** Obtenci&oacute;n de coordenadas Maven y Configuraci&oacute;n de librer&iacute;a Java JWT<br>
![Coordenada JWT Maven](https://github.com/georgeous497git/restful-jwt/blob/master/img/05_mvn_jwt.png)
![Configuracion Libreria JWT](https://github.com/georgeous497git/restful-jwt/blob/master/img/06_dependency_mvn.png)
________________________________________________________________________________________________________________________

## 2. Codificaci&oacute;n
<br><br>
**_i. Configuraci&oacute;n de Contexto para Web Service_**

Gracias a la configuraci&oacute;n del _Starter Web_ de Spring Boot el cual ejecuta un servidor Tomcat, s&oacute;lo es necesario configurar el Bean para personalizar caracter&iacute;sticas del servidor web.

La configuraci&oacute;n del contexto de la aplicaci&oacute;n se declara en la clase principal de SpringBoot haciendo uso de la anotaci&oacute;n `@Bean` declarando el path del contexto con el m&eacute;todo `setContextPath("/WsRest")`:

	@SpringBootApplication
	public class RestfulApplication {

		public static void main(String[] args) {
			SpringApplication.run(RestfulApplication.class, args);
		}

		@Bean
		public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
			return factory -> factory.setContextPath("/WsRest");
		}
	}
<br>
La correcta configuraci&oacute;n del contexto se puede validar ejecutando el proyecto el cual informar&aacute; en el log como se muestra a continuaci&oacute;n.

![Log Contexto](https://github.com/georgeous497git/restful-jwt/blob/master/img/contexto_log.png)

<br><br>
**_ii. Exposici&oacute;n de Web Service (Clase Java)_**

Se genera la clase con la que se expondr&aacute; el Web Service con ayuda de la anotaci&oacute;n `@RestController`

	@RestController
	public class RestWS {

	}

Esta ser&aacute; la &uacute;nica anotaci&oacute;n que necesitamos para poder declarar la clase java destinada a exponer las operaciones Rest.

Dentro de esta clase vamos a inyectar con ayuda de la anotaci&oacute;n `@Autowired` la instancia `AplicativoSrv` en la cual se declaran los m&eacute;todos que contienen la l&oacute;gica de la aplicaci&oacute;n y tareas intermedias antes de generar el Token.


<br><br>
**_iii. Generaci&oacute;n de operaciones Rest del Web Service_**

Con ayuda de las anotaci&oacute;nes `@GetMapping` y `@PostMapping` vamos a declarar las operaciones del Web Service.

_Es importante saber que existen m&aacute;s anotaciones para declarar operaciones Rest las cuales por el momento no explicaremos ya que nos centraremos en explicar el uso de la librer&iacute;a JWT._

### signUp

La primera operaci&oacute;n Rest a declarar ser&aacute; de tipo GET acompa&ntilde;ado del par&aacute;metro `("/signUp")` el cual declara cual va a ser el nombre de la operaci&oacute;n.
Es aqu&iacute; donde se hace uso de la instancia `AplicativoSrv` para invocar el m&eacute;todo `guardaUsuario` que recibe como par&aacute;metro el objeto `SignUpRequest` el cual se espera como Payload en el llamado de la funci&oacute;n gracias a la anotaci&oacute;n `@RequestBody`

	/** Operacion para registrar al usuario, en este punto aun no interviene el Token **/
	@GetMapping("/signUp")
	public @ResponseBody SignUpResponse signUp(@RequestBody SignUpRequest request) {
		
		LOGGER.debug("Consultando /signUp");
		
		aplicativoSrv.guardaUsuario(request);
		
		SignUpResponse signUp = new SignUpResponse();
		signUp.setCodigo(String.valueOf(HttpStatus.ACCEPTED));
		signUp.setMensaje(
				String.format("El usuario: [%s] a sido dado de alta con el correo: [%s]", request.getUsuario(), request.getCorreo()));
		
		return signUp;
	}

Este m&eacute;todo ser&aacute; el primero a ser consultado para registrar un usuario candidato a generar un token con la librer&iacute;a JWT y que devuelve un objeto `SignUpResponse`.


### signIn

Esta operaci&oacute;n Rest se declara de tipo POST acompa&ntilde;ado del par&aacute;metro `("/signIn")` para indicar el nombre de la operaci&oacute;n. Este m&eacute;todo ser&aacute; el segundo a ser consultado para validar datos del usuario y generar el token que ser&aacute; retornado en el objeto `TokenResponse`.

	/** Operacion para validar al usuario previamente registrado y generar el token que es devuelto como respuesta **/
	@PostMapping("/signIn")
	public @ResponseBody TokenResponse signIn(@RequestBody SignInRequest request) {
		
		TokenResponse tokenResponse = aplicativoSrv.identificaUsuario(request);
	
		return tokenResponse;
	}

En el c&oacute;digo anterior podemos ver el uso de la instancia del servicio `AplicativoSrv` para invocar el m&eacute;todo `identificaUsuario` el cual transfiere la instancia `SignInRequest` al componente `ManagerTokenJWT` que contiene la l&oacute;gica de la librer&iacute;a JWT para generar el Token con la l&iacute;nea `token = managerToken.buildToken(issuer);`
<br><br>

	/** Metodo que generar el Token con las directivas de la libreria JWT **/
	public String buildToken(String issuer) {
		
		String token = null;
		
		try {
			/** Para la creacion del token, se hace uso del metodo withIssuer(issuer)
			 * para personalizar el token por cada usuario que ingrese a nuestra aplicación **/
			token = JWT.create()
					.withIssuer(issuer)
					.sign(this.algorithm);
			
			LOGGER.info("Token generado de forma exitosa [{}]", token);
					
		} catch (JWTCreationException e) {
			LOGGER.error("El token no pudo ser generado.");
			e.printStackTrace();
		}
		
		return token;
	}

En el cuerpo de la clase `ManagerTokenJWT` encontramos el m&eacute;todo `public String buildToken(String issuer)` donde se crea la instancia `JWTCreator` invocando la instrucci&oacute;n `JWT.create()`.
En la creaci&oacute;n de la instancia JWTCreator vamos a realizar el llamado a dos m&eacute;todos complementarios, donde el m&eacute;todo `withIssuer` que recibe como par&aacute;metro una cadena (nombre del usuario), nos ayudar&aacute; a personalizar el token por cada usuario que ingrese a nuestra aplicaci&oacute;n.

El m&eacute;todo `sign` que recibe como par&aacute;metro una instancia `Algorithm`, nos servir&aacute; para obtener el Token seg&uacute;n el algoritmo de encriptaci&oacute;n que hayamos elegido y que este deber&aacute; ser el mismo que se use para la validaci&oacute;n del Token como se explicar&aacute; m&aacute;s adelante.
		
La l&iacute;nea de c&oacute;digo `this.algorithm` est&aacute; definida por la instancia al algoritmo `HMAC256` donde se pasa como par&aacute;metro una cadena (`"cl4v3_s3cr3t4"`) que nosotros definimos para el uso en la aplicaci&oacute;n.
	
	private static final String SECRET = "cl4v3_s3cr3t4";
	private Algorithm algorithm = Algorithm.HMAC256(SECRET);	
	

Si no existe alg&uacute;n error al generar el Token, la cadena del token ser&aacute; almacenada en un variable de tipo `String`, de lo contrario se lanzar&aacute; una excepci&oacute;n de tipo `JWTCreationException`.
	
	
### consultarServicio

La &uacute;ltima operaci&oacute;n a declarar ser&aacute; de tipo GET acompa&ntilde;ado del par&aacute;metro `("/consulta")` para indicar el nombre de la operaci&oacute;n.

	@PostMapping("/consulta")
	public ServicioResponse consultarServicio(@RequestBody PayloadRequest payloadRequest, @RequestHeader String usuario, @RequestHeader String token) {

		ServicioResponse servicioResponse = aplicativoSrv.consultarServicio(payloadRequest, usuario, token);

		return servicioResponse;

	}

Se hace uso de la instancia `AplicativoSrv` para invocar el m&eacute;todo `consultarServicio` que recibe como par&aacute;metros el objeto `PayloadRequest` y dos objetos String, estos &uacute;ltimos con la anotaci&oacute;n `@RequestHeader`, estas instancias se transfieren al componente `ManagerTokenJWT` que contiene la l&oacute;gica de la librer&iacute;a JWT para validar el Token con la l&iacute;nea `managerToken.verificaToken(usuario, token);`

	public Map<String, Object> verificaToken(String usuario, String token) {
		
		Map<String, Object> mapObjetos = new HashMap<>();
		mapObjetos.put(TOKEN_VALIDO, false);
		
		try {
			JWTVerifier verifier = JWT.require(this.algorithm)
			        .withIssuer(usuario)
			        .build();

		    DecodedJWT jwt = verifier.verify(token);
		    
		    String usuarioToken = jwt.getIssuer();
		    
		    mapObjetos.put(USUARIO, usuarioToken);
		    mapObjetos.put(TOKEN_VALIDO, true);
		    
		    LOGGER.info("El token es valido.");

		} catch(IllegalArgumentException ie) {
			LOGGER.error("El algoritmo especificado para validar no es el mismo con el cual fue generado el Token. [{}]", ie.getMessage());
			
		} catch (AlgorithmMismatchException ae) {
			LOGGER.error("El algoritmo especificado para validar es nulo. [{}]", ae.getMessage());
			
		} catch(JWTVerificationException ve) {
			LOGGER.error("El token no se verifico de manera correcta. [{}]", ve.getMessage());
		}		
		
		return mapObjetos;
	}

En el cuerpo de la clase `ManagerTokenJWT` encontramos el m&eacute;todo `verificaToken(String usuario, String token)` donde se crea la instancia `JWTVerifier` invocando la instrucci&oacute;n `JWT.require()` que recibe como par&aacute;metro una instancia `Algorithm`, la cual deber&aacute; ser la misma que se us&oacute; para generar el Token.
En la creaci&oacute;n de la instancia JWTVerifier vamos a realizar el llamado a dos m&eacute;todos complementarios, donde el m&eacute;todo `withIssuer` que recibe como par&aacute;metro una cadena (nombre del usuario), nos ayudar&aacute; a verificar que corresponde al token recibido en el Header del llamado Rest.

El m&eacute;todo `build`, nos servir&aacute; para obtener la instancia de tipo JWTVerifier

	JWTVerifier verifier = JWT.require(this.algorithm)
							.withIssuer(usuario)
							.build();

Como &uacute;ltimo se hace el llamado al m&eacute;todo `verify` que recibe como par&aacute;metro la cadena del token que deseamos verificar y que nos regresa una instancia de tipo `DecodedJWT`.

	DecodedJWT jwt = verifier.verify(token);

La instancia DecodedJWT obtenida la usaremos para invocar su m&eacute;todo `getIssuer` del cual obtenemos una instancia String para conocer el _issuer_ encriptado en el token.

En caso de que exista alg&uacute;n error al verificar el Token, es posible obtener las siguientes excepciones:
	
	**AlgorithmMismatchException** - si el algoritmo especificado para validar no es el mismo con el cual fue generado el Token.
	**IllegalArgumentException** - si el algoritmo especificado el `null`.
	**JWTVerificationException** - si alguno de los pasos de validaci&oacute;n del Token falla.
	

## 3. Ejecuci&oacute;n del Web Service

Para este apartado haremos uso de la herramienta Postman para poder realizar las consultas del cliente Rest.

<br><br>
La primera petici&oacute;n (`GET`) ha realizar ser&aacute; como se muestra a continuaci&oacute;n:

![Operacion signUp](https://github.com/georgeous497git/restful-jwt/blob/master/img/signUp.png)

La respuesta esperada es:

![Response signUp](https://github.com/georgeous497git/restful-jwt/blob/master/img/response_signUp.png)

El log que escribe la aplicaci&oacute;n tras este petici&oacute;n es:

	INFO 4460 --- [nio-8080-exec-2] o.a.c.c.C.[.[localhost].[/WsRest]        : Initializing Spring DispatcherServlet 'dispatcherServlet'
	INFO 4460 --- [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
	INFO 4460 --- [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : Completed initialization in 13 ms
	INFO 4460 --- [nio-8080-exec-2] m.c.l.springboot.services.AplicativoSrv  : Guardando el usuario [lambda497]

<br><br>
La segunda petici&oacute;n (`POST`) ha realizar ser&aacute; como se muestra a continuaci&oacute;n:

![Operacion signIn](https://github.com/georgeous497git/restful-jwt/blob/master/img/signIn.png)

La respuesta esperada es:

![Response signIn](https://github.com/georgeous497git/restful-jwt/blob/master/img/response_signIn.png)

El log que escribe la aplicaci&oacute;n tras este petici&oacute;n es:

	INFO 4460 --- [nio-8080-exec-5] m.c.l.springboot.jwt.ManagerTokenJWT     : Token generado de forma exitosa [eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsYW1iZGE0OTcifQ.prbwQxaATgzci_Yabco5N1UJvJxMid17LINJXPHXEdM]

<br><br>
La &uacute;ltima petici&oacute;n (`GET`) ha realizar ser&aacute; como se muestra a continuaci&oacute;n:

**Body**
<br>
![Operacion consulta body](https://github.com/georgeous497git/restful-jwt/blob/master/img/consulta_1.png)

**Header**
<br>
![Operacion consulta header](https://github.com/georgeous497git/restful-jwt/blob/master/img/consulta_2.png)

La respuesta esperada es:

![Response consulta](https://github.com/georgeous497git/restful-jwt/blob/master/img/response_consulta.png)

El log que escribe la aplicaci&oacute;n tras este petici&oacute;n es:

	INFO 10916 --- [nio-8080-exec-4] m.c.l.springboot.jwt.ManagerTokenJWT     : El token es valido.
	INFO 10916 --- [nio-8080-exec-4] m.c.l.springboot.services.AplicativoSrv  : El usuario [lambda497] tiene permisos para consultar el servicio.

## 4. Resumen

El ejemplo anterior representa el ciclo de vida de un JWT Token donde primero el cliente realiza una petici&oacute;n al servidor solicitando un Token donde es muy probable que tenga que enviar las credenciales necesarios (usuario y password).

Seguido de esto, si las credenciales enviadas por el usuario son correctas el servidor responde a la petici&oacute;n con &eacute;xito acompa&ntilde;ado del Token JWT, el cual el cliente deber&aacute; almacenar durante el tiempo de la sesi&oacute;n.

El Token recibido por el cliente ser&aacute; la llave de acceso a las operaciones disponibles en el servidor.

_Muy independiente del tipo de operaci&oacute;n consultada por el cliente el JWT Token deber&aacute; ser enviada en la petici&oacute;n como argumento de tipo Header._

En caso de que el Token sea validado con &eacute;xito el servidor podr&a; procesar la petici&oacute;n solicitada y responder un resultado favorable.

![Lifecyle JWT](https://github.com/georgeous497git/restful-jwt/blob/master/img/lifecycle_jwt.png)


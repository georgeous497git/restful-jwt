package mx.com.lambda.springboot.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import mx.com.lambda.springboot.request.SignInRequest;
import mx.com.lambda.springboot.request.SignUpRequest;
import mx.com.lambda.springboot.request.PayloadRequest;
import mx.com.lambda.springboot.response.ServicioResponse;
import mx.com.lambda.springboot.response.SignUpResponse;
import mx.com.lambda.springboot.response.TokenResponse;
import mx.com.lambda.springboot.services.AplicativoSrv;

@RestController
public class RestWS {
	
	private static Logger LOGGER = LoggerFactory.getLogger(RestWS.class);
	
	@Autowired AplicativoSrv aplicativoSrv;
	
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
	
	/** Operacion para validar al usuario previamente registrado y generar el token que es devuelto como respuesta **/
	@PostMapping("/signIn")
	public @ResponseBody TokenResponse signIn(@RequestBody SignInRequest request) {
		
		TokenResponse tokenResponse = aplicativoSrv.identificaUsuario(request);
	
		return tokenResponse;
	}
	
	@PostMapping("/consulta")
	public ServicioResponse consultarServicio(@RequestBody PayloadRequest payloadRequest, @RequestHeader String usuario, @RequestHeader String token) {
		
		ServicioResponse servicioResponse = aplicativoSrv.consultarServicio(payloadRequest, usuario, token);
		
		return servicioResponse;
		
	}
	
}
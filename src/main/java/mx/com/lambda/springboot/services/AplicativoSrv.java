package mx.com.lambda.springboot.services;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.com.lambda.springboot.jwt.ManagerTokenJWT;
import mx.com.lambda.springboot.request.PayloadRequest;
import mx.com.lambda.springboot.request.SignInRequest;
import mx.com.lambda.springboot.request.SignUpRequest;
import mx.com.lambda.springboot.response.ServicioResponse;
import mx.com.lambda.springboot.response.TokenResponse;

@Service
public class AplicativoSrv {
	private static Logger LOGGER = LoggerFactory.getLogger(AplicativoSrv.class);
			
	@Autowired ManagerTokenJWT managerToken;
	private Map<String, String> mapaUsuarios = new HashMap<>();

	
	/** Este metodo antes de generar el token se valida que el usuario se haya 
	 *  registrado previamente haciendo uso del END POINT "/signUp"
	 *  
	 *  Tu lo podrias utilizar haciendo una consulta en base de datos
	 *  
	 *  **/
	public TokenResponse identificaUsuario(SignInRequest request) {
		
		String token = null;		
		String issuer = request.getUsuario();
		
		TokenResponse tokenResponse = new TokenResponse();
		
		/* Si el usuario si esta registrado se continua para construir el token */
		if(validaUsuario(issuer)) {
			
			/* Metodo existente en el @Component ManagerTokenJWT que genera el token con la herramienta JWT */
			token = managerToken.buildToken(issuer);
			
			if(token == null) {
				tokenResponse.setCodigo("01");
				
			} else {
				tokenResponse.setCodigo("00");
				tokenResponse.setToken(token);
			}
			
		} else {
			LOGGER.error("El usuario no existe. Debe registrarse consultando primero la operacion /signUp");
		}
		
		return tokenResponse;		
	}
	
	public ServicioResponse consultarServicio(PayloadRequest payloadRequest, String usuario, String token) {
		
		Map<String, Object> mapObjetos = managerToken.verificaToken(usuario, token);
		boolean isTokenValido = (boolean)mapObjetos.get("tokenValido");
		
		ServicioResponse servicioResponse = new ServicioResponse();
		
		if(isTokenValido) {
			servicioResponse.setCodigo("00");
			servicioResponse.setDescripcion("El usuario tiene permisos para consultar el servicio");

			LOGGER.info("El usuario [{}] tiene permisos para consultar el servicio.", usuario);
		} else {
			servicioResponse.setCodigo("101");
			servicioResponse.setDescripcion("El usuario NO tiene permisos para consultar el servicio");
			
			LOGGER.info("El usuario [{}] NO tiene permisos para consultar el servicio.", usuario);
		}
		
		return servicioResponse;
	}	

	/** Metodo de utileria para almacenar los datos del usuario
	 * que consulta la operacion /signIn **/
	public void guardaUsuario(SignUpRequest request) {
		String usuario = request.getUsuario();
		
		LOGGER.info("Guardando el usuario [{}]", usuario);
		this.mapaUsuarios.put("usuario", usuario);
	}
	
	
	/** Metodo de utileria que valida la existencia del usuario **/
	private boolean validaUsuario(String usuario) {
		boolean isValido = this.mapaUsuarios.containsValue(usuario);
				
		return isValido;
	}
}

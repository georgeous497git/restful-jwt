package mx.com.lambda.springboot.jwt;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class ManagerTokenJWT {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ManagerTokenJWT.class);

	private static final String SECRET = "cl4v3_s3cr3t4";
	private static final String USUARIO = "usuario";
	private static final String TOKEN_VALIDO = "tokenValido";
	private Algorithm algorithm = Algorithm.HMAC256(SECRET);
	
	
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
}

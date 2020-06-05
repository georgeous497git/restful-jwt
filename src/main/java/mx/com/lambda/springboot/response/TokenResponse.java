package mx.com.lambda.springboot.response;

public class TokenResponse {
	
	private String codigo;
	private String token;

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
